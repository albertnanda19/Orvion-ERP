package com.orvion.finance.domain.event;

import com.orvion.common.event.DomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InvoiceApprovedEvent extends DomainEvent {

    private final String invoiceId;
    private final String invoiceNumber;
    private final String approvedBy;
    private final BigDecimal totalAmount;

    public InvoiceApprovedEvent(String invoiceId, String invoiceNumber,
                                String approvedBy, BigDecimal totalAmount, String tenantId) {
        super("INVOICE_APPROVED", tenantId, invoiceId, "INVOICE");
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.approvedBy = approvedBy;
        this.totalAmount = totalAmount;
    }
}
