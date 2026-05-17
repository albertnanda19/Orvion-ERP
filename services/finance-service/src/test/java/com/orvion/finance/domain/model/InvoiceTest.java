package com.orvion.finance.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.finance.domain.model.enums.InvoiceStatus;
import com.orvion.finance.domain.model.enums.InvoiceType;
import com.orvion.finance.domain.model.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceTest {

    private Invoice invoice;

    @BeforeEach
    void setUp() {
        invoice = new Invoice("tenant1", InvoiceType.ACCOUNTS_RECEIVABLE,
            "CUST001", "Test Customer",
            Instant.now(), Instant.now().plus(30, ChronoUnit.DAYS),
            "IDR", "Test invoice");
    }

    @Test
    void testCreateInvoice() {
        assertNotNull(invoice.getId());
        assertEquals(InvoiceStatus.DRAFT, invoice.getStatus());
        assertTrue(invoice.getInvoiceNumber().startsWith("INV-"));
    }

    @Test
    void testAddLineItem() {
        InvoiceLineItem line = new InvoiceLineItem("Service Fee",
            new BigDecimal("2"), new BigDecimal("500000.0000"),
            new BigDecimal("11"), "IDR");
        invoice.addLineItem(line);

        assertEquals(1, invoice.getLineItems().size());
        assertEquals(new BigDecimal("1000000.0000"), invoice.getSubtotal().getAmount());
        assertEquals(new BigDecimal("110000.0000"), invoice.getTaxAmount().getAmount());
        assertEquals(new BigDecimal("1110000.0000"), invoice.getTotalAmount().getAmount());
    }

    @Test
    void testSubmitForApproval() {
        addSampleLine();
        invoice.submitForApproval();
        assertEquals(InvoiceStatus.PENDING_APPROVAL, invoice.getStatus());
    }

    @Test
    void testSubmitForApprovalFailsOnNoLines() {
        assertThrows(BusinessException.class, () -> invoice.submitForApproval());
    }

    @Test
    void testApprove() {
        addSampleLine();
        invoice.submitForApproval();
        invoice.approve("user1");
        assertEquals(InvoiceStatus.APPROVED, invoice.getStatus());
        assertEquals("user1", invoice.getApprovedBy());
        assertNotNull(invoice.getApprovedAt());
    }

    @Test
    void testApproveFailsWhenNotPending() {
        assertThrows(BusinessException.class, () -> invoice.approve("user1"));
    }

    @Test
    void testApplyPayment() {
        addSampleLine();
        invoice.submitForApproval();
        invoice.approve("user1");

        Money payment = new Money(new BigDecimal("500000.0000"), "IDR");
        invoice.applyPayment(payment);

        assertEquals(InvoiceStatus.PARTIALLY_PAID, invoice.getStatus());
        assertEquals(new BigDecimal("500000.0000"), invoice.getPaidAmount().getAmount());
        assertEquals(new BigDecimal("610000.0000"), invoice.getOutstandingAmount().getAmount());
    }

    @Test
    void testApplyFullPayment() {
        addSampleLine();
        invoice.submitForApproval();
        invoice.approve("user1");

        Money payment = new Money(invoice.getTotalAmount().getAmount(), "IDR");
        invoice.applyPayment(payment);

        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        assertEquals(invoice.getTotalAmount().getAmount(), invoice.getPaidAmount().getAmount());
        assertTrue(invoice.getOutstandingAmount().isZero());
    }

    @Test
    void testVoidFailsWhenPartiallyPaid() {
        addSampleLine();
        invoice.submitForApproval();
        invoice.approve("user1");
        invoice.applyPayment(new Money(new BigDecimal("100000.0000"), "IDR"));

        assertThrows(BusinessException.class, () -> invoice.voidInvoice("Test reason"));
    }

    @Test
    void testVoidSucceedsWhenDraft() {
        invoice.voidInvoice("Cancelled by customer");
        assertEquals(InvoiceStatus.VOID, invoice.getStatus());
    }

    private void addSampleLine() {
        InvoiceLineItem line = new InvoiceLineItem("Service Fee",
            new BigDecimal("2"), new BigDecimal("500000.0000"),
            new BigDecimal("11"), "IDR");
        invoice.addLineItem(line);
    }
}
