package com.orvion.finance.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.finance.domain.model.enums.InvoiceStatus;
import com.orvion.finance.domain.model.enums.InvoiceType;
import com.orvion.finance.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoices", indexes = {
    @Index(name = "idx_inv_tenant_status", columnList = "tenantId, status"),
    @Index(name = "idx_inv_tenant_counterparty", columnList = "tenantId, counterpartyId"),
    @Index(name = "idx_inv_number", columnList = "invoiceNumber", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class Invoice extends Auditable {

    @Id
    private UUID id;

    @Column(name = "tenant_id", length = 50, nullable = false)
    private String tenantId;

    @Column(name = "invoice_number", length = 50, nullable = false)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 25, nullable = false)
    private InvoiceType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Column(name = "counterparty_id", length = 50)
    private String counterpartyId;

    @Column(name = "counterparty_name", length = 200)
    private String counterpartyName;

    @Column(name = "issue_date")
    private Instant issueDate;

    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "subtotal_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "subtotal_currency", length = 3))
    })
    private Money subtotal;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "tax_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "tax_currency", length = 3))
    })
    private Money taxAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount", precision = 19, scale = 4, nullable = false)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "total_currency", length = 3, nullable = false))
    })
    private Money totalAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "paid_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "paid_currency", length = 3))
    })
    private Money paidAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "outstanding_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "outstanding_currency", length = 3))
    })
    private Money outstandingAmount;

    @Column(length = 3)
    private String currency;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lineNumber ASC")
    private List<InvoiceLineItem> lineItems = new ArrayList<>();

    @Column(name = "payment_ids")
    private String paymentIds; // JSON array of UUIDs

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    public Invoice(String tenantId, InvoiceType type, String counterpartyId, String counterpartyName,
                   Instant issueDate, Instant dueDate, String currency, String notes) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.invoiceNumber = generateInvoiceNumber();
        this.type = type;
        this.status = InvoiceStatus.DRAFT;
        this.counterpartyId = counterpartyId;
        this.counterpartyName = counterpartyName;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.currency = currency;
        this.notes = notes;
        this.lineItems = new ArrayList<>();
        this.subtotal = Money.zero(currency);
        this.taxAmount = Money.zero(currency);
        this.totalAmount = Money.zero(currency);
        this.paidAmount = Money.zero(currency);
        this.outstandingAmount = Money.zero(currency);
    }

    public void addLineItem(InvoiceLineItem item) {
        if (status != InvoiceStatus.DRAFT && status != InvoiceStatus.PENDING_APPROVAL) {
            throw new BusinessException("INVOICE_NOT_EDITABLE",
                "Cannot modify invoice in status: " + status);
        }
        item.setInvoice(this);
        item.setLineNumber(this.lineItems.size() + 1);
        this.lineItems.add(item);
        recalculateTotals();
    }

    public void approve(String approverUserId) {
        if (this.status != InvoiceStatus.PENDING_APPROVAL) {
            throw new BusinessException("INVOICE_NOT_PENDING_APPROVAL",
                "Cannot approve invoice in status: " + this.status);
        }
        this.status = InvoiceStatus.APPROVED;
        this.approvedBy = approverUserId;
        this.approvedAt = Instant.now();
    }

    public void applyPayment(Money amount) {
        if (this.status == InvoiceStatus.PAID || this.status == InvoiceStatus.CANCELLED
            || this.status == InvoiceStatus.VOID) {
            throw new BusinessException("INVOICE_CLOSED",
                "Cannot apply payment to invoice in status: " + this.status);
        }
        this.paidAmount = this.paidAmount.add(amount);
        this.outstandingAmount = this.totalAmount.subtract(this.paidAmount);

        if (this.outstandingAmount.isZero() || this.outstandingAmount.isNegative()) {
            this.status = InvoiceStatus.PAID;
            this.outstandingAmount = Money.zero(this.currency);
        } else {
            this.status = InvoiceStatus.PARTIALLY_PAID;
        }
    }

    public void markOverdue() {
        if (dueDate.isAfter(Instant.now())) {
            throw new BusinessException("INVOICE_NOT_DUE", "Invoice is not yet due");
        }
        if (status == InvoiceStatus.PAID || status == InvoiceStatus.CANCELLED
            || status == InvoiceStatus.VOID) {
            throw new BusinessException("INVOICE_CLOSED",
                "Cannot mark overdue invoice in status: " + status);
        }
        this.status = InvoiceStatus.OVERDUE;
    }

    public void voidInvoice(String reason) {
        if (status == InvoiceStatus.PAID || status == InvoiceStatus.PARTIALLY_PAID) {
            throw new BusinessException("INVOICE_HAS_PAYMENTS",
                "Cannot void an invoice with payments. Reverse payments first.");
        }
        if (status == InvoiceStatus.CANCELLED || status == InvoiceStatus.VOID) {
            throw new BusinessException("INVOICE_ALREADY_VOID", "Invoice is already void/cancelled");
        }
        this.status = InvoiceStatus.VOID;
        this.notes = (this.notes != null ? this.notes + "\n" : "") + "Voided: " + reason;
    }

    public void submitForApproval() {
        if (this.status != InvoiceStatus.DRAFT) {
            throw new BusinessException("INVOICE_NOT_DRAFT",
                "Cannot submit invoice in status: " + this.status);
        }
        if (this.lineItems.isEmpty()) {
            throw new BusinessException("INVOICE_NO_LINES",
                "Cannot submit invoice with no line items");
        }
        this.status = InvoiceStatus.PENDING_APPROVAL;
    }

    private void recalculateTotals() {
        Money newSubtotal = Money.zero(this.currency);
        Money newTax = Money.zero(this.currency);

        for (InvoiceLineItem item : this.lineItems) {
            newSubtotal = newSubtotal.add(item.getSubtotal());
            newTax = newTax.add(item.getTaxAmount());
        }

        this.subtotal = newSubtotal;
        this.taxAmount = newTax;
        this.totalAmount = newSubtotal.add(newTax);
        this.outstandingAmount = this.totalAmount.subtract(this.paidAmount);
    }

    public static String generateInvoiceNumber() {
        int year = Year.now().getValue();
        long sequence = System.currentTimeMillis() % 1000000;
        return String.format("INV-%04d-%06d", year, sequence);
    }

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (invoiceNumber == null) {
            invoiceNumber = generateInvoiceNumber();
        }
    }
}
