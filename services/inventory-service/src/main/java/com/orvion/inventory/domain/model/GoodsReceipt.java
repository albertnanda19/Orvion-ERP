package com.orvion.inventory.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.inventory.domain.model.enums.GoodsReceiptStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "goods_receipts", indexes = {
    @Index(name = "idx_gr_tenant_number", columnList = "tenantId, receiptNumber", unique = true),
    @Index(name = "idx_gr_tenant_po", columnList = "tenantId, purchaseOrderId")
})
@Getter @Setter @NoArgsConstructor
public class GoodsReceipt extends Auditable {
    @Id
    private UUID id;
    
    @Column(length = 50, nullable = false)
    private String tenantId;
    
    @Column(length = 50, nullable = false)
    private String receiptNumber;
    
    @Column(nullable = false)
    private UUID purchaseOrderId;
    
    @Column(nullable = false)
    private UUID warehouseId;
    
    @Column(length = 50)
    private String receivedBy;
    
    @Column
    private Instant receivedAt;
    
    @OneToMany(mappedBy = "goodsReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoodsReceiptLine> lines = new ArrayList<>();
    
    @Column(length = 1000)
    private String notes;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private GoodsReceiptStatus status = GoodsReceiptStatus.DRAFT;

    public GoodsReceipt(String tenantId, UUID purchaseOrderId, UUID warehouseId, String receivedBy, Instant receivedAt, String notes) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.receiptNumber = "GR-" + Instant.now().toEpochMilli();
        this.purchaseOrderId = purchaseOrderId;
        this.warehouseId = warehouseId;
        this.receivedBy = receivedBy;
        this.receivedAt = receivedAt != null ? receivedAt : Instant.now();
        this.notes = notes;
        this.status = GoodsReceiptStatus.DRAFT;
    }
    
    public void confirm() {
        if (status != GoodsReceiptStatus.DRAFT)
            throw new BusinessException("INVALID_STATUS", "Cannot confirm goods receipt in status: " + status);
        if (lines.isEmpty())
            throw new BusinessException("EMPTY_GR", "Cannot confirm a goods receipt with no lines");
        this.status = GoodsReceiptStatus.CONFIRMED;
    }
}
