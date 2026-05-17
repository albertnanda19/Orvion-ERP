package com.orvion.inventory.domain.model;

import com.orvion.inventory.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_entries")
@Getter @Setter @NoArgsConstructor
public class StockEntry {
    @Id
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal remainingQuantity;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "unit_cost", precision = 18, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "cost_currency", length = 3))
    })
    private Money unitCost;
    
    @Column(nullable = false)
    private Instant receivedAt;

    public StockEntry(Product product, BigDecimal quantity, Money unitCost) {
        this.id = UUID.randomUUID();
        this.product = product;
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        this.unitCost = unitCost;
        this.receivedAt = Instant.now();
    }
    
    public void consume(BigDecimal qty) {
        if (qty.compareTo(remainingQuantity) > 0)
            throw new IllegalArgumentException("Cannot consume more than remaining");
        this.remainingQuantity = this.remainingQuantity.subtract(qty);
    }
}
