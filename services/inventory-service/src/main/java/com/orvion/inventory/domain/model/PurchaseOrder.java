package com.orvion.inventory.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.inventory.domain.model.enums.PurchaseOrderStatus;
import com.orvion.inventory.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "purchase_orders", indexes = {
    @Index(name = "idx_po_tenant_number", columnList = "tenantId, poNumber", unique = true),
    @Index(name = "idx_po_tenant_status", columnList = "tenantId, status"),
    @Index(name = "idx_po_tenant_supplier", columnList = "tenantId, supplierId")
})
@Getter @Setter @NoArgsConstructor
public class PurchaseOrder extends Auditable {
    @Id
    private UUID id;
    
    @Column(length = 50, nullable = false)
    private String tenantId;
    
    @Column(length = 50, nullable = false)
    private String poNumber;
    
    @Column(length = 50, nullable = false)
    private String supplierId;
    
    @Column(length = 200)
    private String supplierName;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;
    
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderLine> lines = new ArrayList<>();
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount", precision = 18, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "currency", length = 3))
    })
    private Money totalAmount;
    
    @Column
    private Instant orderDate;
    
    @Column
    private Instant expectedDelivery;
    
    @Column(length = 50)
    private String approvedBy;
    
    @Column
    private Instant approvedAt;
    
    @Column(length = 1000)
    private String notes;

    public PurchaseOrder(String tenantId, String supplierId, String supplierName, Instant orderDate, Instant expectedDelivery, String notes) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.poNumber = generatePONumber();
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.status = PurchaseOrderStatus.DRAFT;
        this.orderDate = orderDate != null ? orderDate : Instant.now();
        this.expectedDelivery = expectedDelivery;
        this.notes = notes;
        this.totalAmount = Money.zero("IDR");
    }
    
    public void approve(String approverId) {
        if (lines.isEmpty()) throw new BusinessException("EMPTY_PO", "Cannot approve a PO with no lines");
        if (status != PurchaseOrderStatus.DRAFT && status != PurchaseOrderStatus.PENDING_APPROVAL)
            throw new BusinessException("INVALID_STATUS", "Cannot approve PO in status: " + status);
        this.status = PurchaseOrderStatus.APPROVED;
        this.approvedBy = approverId;
        this.approvedAt = Instant.now();
    }
    
    public void addLine(PurchaseOrderLine line) {
        lines.add(line);
        recalculateTotal();
    }
    
    public void cancel(String reason) {
        if (status != PurchaseOrderStatus.DRAFT && status != PurchaseOrderStatus.PENDING_APPROVAL)
            throw new BusinessException("INVALID_STATUS", "Cannot cancel PO in status: " + status);
        this.status = PurchaseOrderStatus.CANCELLED;
    }
    
    public void markSent() {
        if (status != PurchaseOrderStatus.APPROVED)
            throw new BusinessException("INVALID_STATUS", "Cannot mark as sent PO in status: " + status);
        this.status = PurchaseOrderStatus.SENT;
    }
    
    private void recalculateTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (PurchaseOrderLine line : lines) {
            if (line.getTotalPrice() != null) {
                sum = sum.add(line.getTotalPrice().getAmount());
            }
        }
        this.totalAmount = new Money(sum, totalAmount != null ? totalAmount.getCurrencyCode() : "IDR");
    }
    
    private static long sequenceCounter = 0;
    public static String generatePONumber() {
        sequenceCounter++;
        return "PO-" + java.time.Year.now().getValue() + "-" + String.format("%06d", sequenceCounter);
    }
}
