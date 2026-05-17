package com.orvion.finance.integration;

import com.orvion.finance.domain.model.Invoice;
import com.orvion.finance.domain.model.InvoiceLineItem;
import com.orvion.finance.domain.model.Payment;
import com.orvion.finance.domain.model.enums.InvoiceStatus;
import com.orvion.finance.domain.model.enums.InvoiceType;
import com.orvion.finance.domain.model.enums.PaymentMethod;
import com.orvion.finance.domain.model.vo.Money;
import com.orvion.finance.domain.repository.InvoiceRepository;
import com.orvion.finance.domain.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class InvoiceIntegrationIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:4.0-management-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void testFullInvoiceLifecycle() {
        String tenantId = "tenant1";

        Invoice invoice = new Invoice(tenantId, InvoiceType.ACCOUNTS_RECEIVABLE,
            "CUST001", "Test Customer",
            Instant.now(), Instant.now().plus(30, ChronoUnit.DAYS), "IDR", "Integration test");

        InvoiceLineItem line = new InvoiceLineItem("Consulting Service",
            new BigDecimal("10"), new BigDecimal("500000.0000"),
            new BigDecimal("11"), "IDR");
        invoice.addLineItem(line);

        invoice = invoiceRepository.save(invoice);
        assertNotNull(invoice.getId());
        assertEquals(InvoiceStatus.DRAFT, invoice.getStatus());
        assertEquals(new BigDecimal("5500000.0000"), invoice.getTotalAmount().getAmount());

        invoice.submitForApproval();
        invoice.approve("approver1");
        invoice = invoiceRepository.save(invoice);
        assertEquals(InvoiceStatus.APPROVED, invoice.getStatus());

        invoice.applyPayment(new Money(new BigDecimal("5500000.0000"), "IDR"));
        invoice = invoiceRepository.save(invoice);
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());

        Payment payment = new Payment(tenantId, invoice.getId(),
            new Money(new BigDecimal("5500000.0000"), "IDR"),
            PaymentMethod.BANK_TRANSFER, Instant.now(),
            "TRX001", "BCA-1234", "Full payment");
        payment = paymentRepository.save(payment);
        assertNotNull(payment.getId());

        Optional<Invoice> fetched = invoiceRepository.findById(invoice.getId());
        assertTrue(fetched.isPresent());
        assertEquals(InvoiceStatus.PAID, fetched.get().getStatus());

        List<Payment> payments = paymentRepository.findByInvoiceId(invoice.getId());
        assertEquals(1, payments.size());
        assertEquals("TRX001", payments.get(0).getReference());
    }

    @Test
    void testApproveInvoiceLifecycle() {
        String tenantId = "tenant1";

        Invoice invoice = new Invoice(tenantId, InvoiceType.ACCOUNTS_PAYABLE,
            "SUPP001", "Test Supplier",
            Instant.now(), Instant.now().plus(45, ChronoUnit.DAYS), "IDR", "AP test");

        InvoiceLineItem line = new InvoiceLineItem("Office Supplies",
            new BigDecimal("100"), new BigDecimal("25000.0000"),
            new BigDecimal("11"), "IDR");
        invoice.addLineItem(line);

        invoice = invoiceRepository.save(invoice);
        assertEquals(InvoiceStatus.DRAFT, invoice.getStatus());

        invoice.submitForApproval();
        invoice.approve("manager1");
        invoice = invoiceRepository.save(invoice);
        assertEquals(InvoiceStatus.APPROVED, invoice.getStatus());
        assertEquals("manager1", invoice.getApprovedBy());
    }
}
