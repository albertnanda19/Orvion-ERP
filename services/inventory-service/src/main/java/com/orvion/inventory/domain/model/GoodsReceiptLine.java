package com.orvion.inventory.domain.model;

import com.orvion.inventory.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "goods_receipt_lines")
@Getter @Setter @NoArgsConstructor
public class GoodsReceiptLine {
    @Id
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_receipt_id", nullable = false)
    private GoodsReceipt goodsReceipt;
    
    @Column(nullable = false)
    private UUID purchaseOrderLineId;
    
    @Column(nullable = false)
    private UUID productId;
    
    @Column(length = 200)
    private String productName;
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal acceptedQuantity;
    
    @Column(precision = 18, scale = 4)
    private BigDecimal rejectedQuantity;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "unit_cost", precision = 18, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "line_cost_currency", length = 3))
    })
    private Money unitCost;

    public GoodsReceiptLine(GoodsReceipt goodsReceipt, UUID purchaseOrderLineId, UUID productId, String productName,
                            BigDecimal quantity, BigDecimal acceptedQuantity, BigDecimal rejectedQuantity, Money unitCost) {
        this.id = UUID.randomUUID();
        this.goodsReceipt = goodsReceipt;
        this.purchaseOrderLineId = purchaseOrderLineId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.acceptedQuantity = acceptedQuantity;
        this.rejectedQuantity = rejectedQuantity;
        this.unitCost = unitCost;
    }
}
