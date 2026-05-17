package com.orvion.inventory.domain.event;
import com.orvion.common.event.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;

public class StockUpdatedEvent extends DomainEvent {
    private final String productId;
    private final String warehouseId;
    private final BigDecimal quantity;
    private final String movementType;
    private final String reference;
    
    public StockUpdatedEvent(String productId, String warehouseId, BigDecimal quantity, String movementType, String reference, String tenantId) {
        super("STOCK_UPDATED", "StockMovement", productId, tenantId);
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.movementType = movementType;
        this.reference = reference;
    }
    
    public String getProductId() { return productId; }
    public String getWarehouseId() { return warehouseId; }
    public BigDecimal getQuantity() { return quantity; }
    public String getMovementType() { return movementType; }
    public String getReference() { return reference; }
}
