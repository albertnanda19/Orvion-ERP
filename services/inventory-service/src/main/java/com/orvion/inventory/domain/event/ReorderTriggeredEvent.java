package com.orvion.inventory.domain.event;
import com.orvion.common.event.DomainEvent;
import java.math.BigDecimal;

public class ReorderTriggeredEvent extends DomainEvent {
    private final String productId;
    private final String productName;
    private final String sku;
    private final BigDecimal currentStock;
    private final BigDecimal reorderQuantity;
    private final String preferredSupplierId;
    
    public ReorderTriggeredEvent(String productId, String productName, String sku, BigDecimal currentStock, BigDecimal reorderQuantity, String preferredSupplierId, String tenantId) {
        super("REORDER_TRIGGERED", "Product", productId, tenantId);
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.currentStock = currentStock;
        this.reorderQuantity = reorderQuantity;
        this.preferredSupplierId = preferredSupplierId;
    }
    
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getSku() { return sku; }
    public BigDecimal getCurrentStock() { return currentStock; }
    public BigDecimal getReorderQuantity() { return reorderQuantity; }
    public String getPreferredSupplierId() { return preferredSupplierId; }
}
