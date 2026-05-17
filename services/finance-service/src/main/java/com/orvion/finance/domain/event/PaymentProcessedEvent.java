package com.orvion.finance.domain.event;

import com.orvion.common.event.DomainEvent;
import com.orvion.finance.domain.model.enums.PaymentMethod;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentProcessedEvent extends DomainEvent {

    private final String paymentId;
    private final String invoiceId;
    private final BigDecimal amount;
    private final PaymentMethod method;

    public PaymentProcessedEvent(String paymentId, String invoiceId,
                                 BigDecimal amount, PaymentMethod method, String tenantId) {
        super("PAYMENT_PROCESSED", tenantId, paymentId, "PAYMENT");
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.method = method;
    }
}
