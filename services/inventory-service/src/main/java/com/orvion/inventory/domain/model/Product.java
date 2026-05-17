package com.orvion.inventory.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.inventory.domain.model.enums.CostingMethod;
import com.orvion.inventory.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_prod_tenant_sku", columnList = "tenantId, sku", unique = true),
    @Index(name = "idx_prod_tenant_category", columnList = "tenantId, category"),
    @Index(name = "idx_prod_tenant_active", columnList = "tenantId, active")
})
@Getter @Setter @NoArgsConstructor
public class Product extends Auditable {
    @Id
    private UUID id;
    
    @Column(length = 50, nullable = false)
    private String tenantId;
    
    @Column(length = 50, nullable = false)
    private String sku;
    
    @Column(length = 200, nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(length = 100)
    private String category;
    
    @Column(length = 20, nullable = false)
    private String unit;
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal currentStock = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal reservedStock = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
    
    @Column(precision = 18, scale = 4)
    private BigDecimal reorderPoint = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
    
    @Column(precision = 18, scale = 4)
    private BigDecimal reorderQuantity = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
    
    @Column(length = 50)
    private String preferredSupplierId;
    
    @Column(length = 50)
    private String warehouseId;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "standard_cost", precision = 18, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "cost_currency", length = 3))
    })
    private Money standardCost;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private CostingMethod costingMethod = CostingMethod.AVERAGE_COST;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("receivedAt ASC")
    private List<StockEntry> stockEntries = new ArrayList<>();

    public Product(String tenantId, String sku, String name, String unit, CostingMethod costingMethod) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.sku = sku;
        this.name = name;
        this.unit = unit;
        this.costingMethod = costingMethod;
        this.currentStock = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        this.reservedStock = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        this.active = true;
    }
    
    public void receiveStock(BigDecimal qty, Money unitCost) {
        if (qty.compareTo(BigDecimal.ZERO) <= 0) throw new BusinessException("INVALID_QTY", "Received quantity must be positive");
        StockEntry entry = new StockEntry(this, qty, unitCost);
        stockEntries.add(entry);
        this.currentStock = this.currentStock.add(qty);
    }
    
    public void issueStock(BigDecimal qty, String reference) {
        if (qty.compareTo(BigDecimal.ZERO) <= 0) throw new BusinessException("INVALID_QTY", "Issued quantity must be positive");
        BigDecimal available = currentStock.subtract(reservedStock);
        if (qty.compareTo(available) > 0)
            throw new BusinessException("INSUFFICIENT_STOCK", "Insufficient available stock. Required: " + qty + ", Available: " + available);
        BigDecimal remaining = qty;
        switch (costingMethod) {
            case FIFO:
                for (StockEntry entry : stockEntries) {
                    if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
                    BigDecimal fromEntry = entry.getRemainingQuantity().min(remaining);
                    entry.consume(fromEntry);
                    remaining = remaining.subtract(fromEntry);
                }
                break;
            case LIFO:
                for (int i = stockEntries.size() - 1; i >= 0; i--) {
                    if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
                    StockEntry entry = stockEntries.get(i);
                    BigDecimal fromEntry = entry.getRemainingQuantity().min(remaining);
                    entry.consume(fromEntry);
                    remaining = remaining.subtract(fromEntry);
                }
                break;
            default:
                BigDecimal avgCost = calculateAverageCost();
                for (StockEntry entry : stockEntries) {
                    if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
                    BigDecimal fromEntry = entry.getRemainingQuantity().min(remaining);
                    entry.consume(fromEntry);
                    remaining = remaining.subtract(fromEntry);
                }
                break;
        }
        if (remaining.compareTo(BigDecimal.ZERO) > 0)
            throw new BusinessException("COSTING_ERROR", "Could not fully consume stock entries");
        this.currentStock = this.currentStock.subtract(qty);
        stockEntries.removeIf(e -> e.getRemainingQuantity().compareTo(BigDecimal.ZERO) <= 0);
    }
    
    public void reserveStock(BigDecimal qty, String reference) {
        if (qty.compareTo(BigDecimal.ZERO) <= 0) throw new BusinessException("INVALID_QTY", "Reservation quantity must be positive");
        BigDecimal available = currentStock.subtract(reservedStock);
        if (qty.compareTo(available) > 0)
            throw new BusinessException("INSUFFICIENT_STOCK", "Insufficient stock to reserve. Required: " + qty + ", Available: " + available);
        this.reservedStock = this.reservedStock.add(qty);
    }
    
    public void releaseReservation(BigDecimal qty, String reference) {
        if (qty.compareTo(BigDecimal.ZERO) <= 0) throw new BusinessException("INVALID_QTY", "Release quantity must be positive");
        if (qty.compareTo(this.reservedStock) > 0)
            throw new BusinessException("INVALID_RELEASE", "Cannot release more than reserved. Reserved: " + this.reservedStock + ", Release: " + qty);
        this.reservedStock = this.reservedStock.subtract(qty);
    }
    
    public boolean isReorderRequired() {
        return currentStock.subtract(reservedStock).compareTo(reorderPoint) <= 0;
    }
    
    public BigDecimal calculateAverageCost() {
        if (stockEntries.isEmpty()) return BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalQty = BigDecimal.ZERO;
        for (StockEntry entry : stockEntries) {
            BigDecimal consumed = entry.getQuantity().subtract(entry.getRemainingQuantity());
            BigDecimal cost = consumed.multiply(entry.getUnitCost().getAmount());
            totalCost = totalCost.add(cost);
            totalQty = totalQty.add(consumed);
        }
        if (totalQty.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return totalCost.divide(totalQty, 4, RoundingMode.HALF_UP);
    }
}
