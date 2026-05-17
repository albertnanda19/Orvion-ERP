package com.orvion.finance.application.usecase;

import com.orvion.finance.application.dto.request.CreateInvoiceLineItemRequest;
import com.orvion.finance.application.dto.request.CreateInvoiceRequest;
import com.orvion.finance.application.dto.response.InvoiceResponse;
import com.orvion.finance.application.mapper.InvoiceMapper;
import com.orvion.finance.application.mapper.PaymentMapper;
import com.orvion.finance.domain.model.Invoice;
import com.orvion.finance.domain.model.InvoiceLineItem;
import com.orvion.finance.domain.model.enums.InvoiceStatus;
import com.orvion.finance.domain.repository.InvoiceRepository;
import com.orvion.finance.domain.repository.PaymentRepository;
import com.orvion.finance.infrastructure.messaging.FinanceEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceUseCaseTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private InvoiceMapper invoiceMapper;
    @Mock
    private PaymentMapper paymentMapper;
    private FinanceEventPublisher eventPublisher;

    private InvoiceUseCase invoiceUseCase;

    @BeforeEach
    void setUp() {
        eventPublisher = mock(FinanceEventPublisher.class);
        invoiceUseCase = new InvoiceUseCase(invoiceRepository, paymentRepository,
            invoiceMapper, paymentMapper, eventPublisher);
    }

    @Test
    void testCreateInvoice() {
        Invoice invoice = new Invoice("tenant1",
            com.orvion.finance.domain.model.enums.InvoiceType.ACCOUNTS_RECEIVABLE,
            "CUST001", "Test Customer",
            Instant.now(), Instant.now().plus(30, ChronoUnit.DAYS),
            "IDR", "Test");
        InvoiceResponse response = InvoiceResponse.builder()
            .id(invoice.getId())
            .invoiceNumber(invoice.getInvoiceNumber())
            .type("ACCOUNTS_RECEIVABLE")
            .status(InvoiceStatus.DRAFT.name())
            .build();

        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(invoiceMapper.toResponse(any(Invoice.class))).thenReturn(response);

        CreateInvoiceRequest request = CreateInvoiceRequest.builder()
            .type("ACCOUNTS_RECEIVABLE")
            .counterpartyId("CUST001")
            .counterpartyName("Test Customer")
            .dueDate(Instant.now().plus(30, ChronoUnit.DAYS))
            .currency("IDR")
            .lineItems(List.of(
                CreateInvoiceLineItemRequest.builder()
                    .description("Service Fee")
                    .quantity(new BigDecimal("1"))
                    .unitPrice(new BigDecimal("1000000.0000"))
                    .taxRate(new BigDecimal("11"))
                    .build()
            ))
            .build();

        InvoiceResponse result = invoiceUseCase.createInvoice("tenant1", "user1", request);

        assertNotNull(result);
        assertEquals("ACCOUNTS_RECEIVABLE", result.getType());
        assertEquals(InvoiceStatus.DRAFT.name(), result.getStatus());
        assertTrue(result.getInvoiceNumber().startsWith("INV-"));

        verify(invoiceRepository, times(1)).save(any(Invoice.class));
        verify(eventPublisher, times(1)).publishInvoiceCreated(any());
    }

    @Test
    void testProcessPayment() {
        Invoice invoice = new Invoice("tenant1",
            com.orvion.finance.domain.model.enums.InvoiceType.ACCOUNTS_RECEIVABLE,
            "CUST001", "Test Customer",
            Instant.now(), Instant.now().plus(30, ChronoUnit.DAYS),
            "IDR", "Test");
        InvoiceLineItem line = new InvoiceLineItem("Service",
            new BigDecimal("1"), new BigDecimal("1110000.0000"),
            BigDecimal.ZERO, "IDR");
        invoice.addLineItem(line);

        com.orvion.finance.domain.model.Payment payment = new com.orvion.finance.domain.model.Payment(
            "tenant1", invoice.getId(),
            new com.orvion.finance.domain.model.vo.Money(new BigDecimal("1110000.0000"), "IDR"),
            com.orvion.finance.domain.model.enums.PaymentMethod.BANK_TRANSFER,
            Instant.now(), "REF001", "BNI-001", "Test payment");

        when(invoiceRepository.findById(invoice.getId())).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(paymentRepository.save(any(com.orvion.finance.domain.model.Payment.class))).thenReturn(payment);
        when(paymentMapper.toResponse(any())).thenReturn(
            com.orvion.finance.application.dto.response.PaymentResponse.builder()
                .id(payment.getId())
                .invoiceId(invoice.getId())
                .amount(new BigDecimal("1110000.0000"))
                .build()
        );

        com.orvion.finance.application.dto.request.ProcessPaymentRequest paymentReq =
            com.orvion.finance.application.dto.request.ProcessPaymentRequest.builder()
                .invoiceId(invoice.getId().toString())
                .amount(new BigDecimal("1110000.0000"))
                .method("BANK_TRANSFER")
                .currency("IDR")
                .build();

        com.orvion.finance.application.dto.response.PaymentResponse response =
            invoiceUseCase.processPayment("tenant1", "user1", paymentReq);

        assertNotNull(response);
        assertEquals(invoice.getId(), response.getInvoiceId());
        assertEquals(new BigDecimal("1110000.0000"), response.getAmount());

        verify(paymentRepository, times(1)).save(any());
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
        verify(eventPublisher, times(1)).publishPaymentProcessed(any());
    }
}
