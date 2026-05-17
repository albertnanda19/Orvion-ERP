package com.orvion.finance.domain.event;

import com.orvion.common.event.DomainEvent;
import com.orvion.finance.domain.model.enums.InvoiceType;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InvoiceCreatedEvent extends DomainEvent {

    private final String invoiceId;
    private final String invoiceNumber;
    private final InvoiceType type;
    private final BigDecimal totalAmount;
    private final String currency;
    private final String counterpartyId;

    public InvoiceCreatedEvent(String invoiceId, String invoiceNumber, InvoiceType type,
                               BigDecimal totalAmount, String currency,
                               String counterpartyId, String tenantId) {
        super("INVOICE_CREATED", tenantId, invoiceId, "INVOICE");
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.type = type;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.counterpartyId = counterpartyId;
    }
}
