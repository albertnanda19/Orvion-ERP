package com.orvion.inventory.domain.model;

import com.orvion.inventory.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "purchase_order_lines")
@Getter @Setter @NoArgsConstructor
public class PurchaseOrderLine {
    @Id
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;
    
    @Column(nullable = false)
    private UUID productId;
    
    @Column(length = 200)
    private String productName;
    
    @Column(length = 50)
    private String sku;
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal receivedQuantity = BigDecimal.ZERO.setScale(4, java.math.RoundingMode.HALF_UP);
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "unit_price", precision = 18, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "line_currency", length = 3))
    })
    private Money unitPrice;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_price", precision = 18, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "total_line_currency", length = 3))
    })
    private Money totalPrice;

    public PurchaseOrderLine(PurchaseOrder purchaseOrder, UUID productId, String productName, String sku,
                             BigDecimal quantity, Money unitPrice) {
        this.id = UUID.randomUUID();
        this.purchaseOrder = purchaseOrder;
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(quantity);
        this.receivedQuantity = BigDecimal.ZERO.setScale(4, java.math.RoundingMode.HALF_UP);
    }
}
