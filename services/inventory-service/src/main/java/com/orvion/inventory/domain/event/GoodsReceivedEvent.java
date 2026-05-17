package com.orvion.inventory.domain.event;
import com.orvion.common.event.DomainEvent;
import java.math.BigDecimal;

public class GoodsReceivedEvent extends DomainEvent {
    private final String receiptNumber;
    private final String purchaseOrderId;
    private final String warehouseId;
    
    public GoodsReceivedEvent(String receiptId, String receiptNumber, String purchaseOrderId, String warehouseId, String tenantId) {
        super("GOODS_RECEIVED", "GoodsReceipt", receiptId, tenantId);
        this.receiptNumber = receiptNumber;
        this.purchaseOrderId = purchaseOrderId;
        this.warehouseId = warehouseId;
    }
    
    public String getReceiptNumber() { return receiptNumber; }
    public String getPurchaseOrderId() { return purchaseOrderId; }
    public String getWarehouseId() { return warehouseId; }
}
