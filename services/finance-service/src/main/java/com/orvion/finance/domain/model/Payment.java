package com.orvion.finance.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.finance.domain.model.enums.PaymentMethod;
import com.orvion.finance.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_invoice", columnList = "invoiceId"),
    @Index(name = "idx_payment_tenant_date", columnList = "tenantId, paymentDate")
})
@Getter
@Setter
@NoArgsConstructor
public class Payment extends Auditable {

    @Id
    private UUID id;

    @Column(name = "tenant_id", length = 50, nullable = false)
    private String tenantId;

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "amount", precision = 19, scale = 4, nullable = false)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "currency", length = 3))
    })
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", length = 20, nullable = false)
    private PaymentMethod method;

    @Column(name = "payment_date", nullable = false)
    private Instant paymentDate;

    @Column(length = 100)
    private String reference;

    @Column(name = "bank_account", length = 100)
    private String bankAccount;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private boolean reconciled = false;

    public Payment(String tenantId, UUID invoiceId, Money amount, PaymentMethod method,
                   Instant paymentDate, String reference, String bankAccount, String notes) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.method = method;
        this.paymentDate = paymentDate;
        this.reference = reference;
        this.bankAccount = bankAccount;
        this.notes = notes;
        this.reconciled = false;
    }
}
