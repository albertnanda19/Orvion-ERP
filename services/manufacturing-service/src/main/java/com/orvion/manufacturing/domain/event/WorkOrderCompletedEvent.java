package com.orvion.manufacturing.domain.event;

import com.orvion.common.event.DomainEvent;

import java.math.BigDecimal;

public class WorkOrderCompletedEvent extends DomainEvent {
    private final String workOrderId;
    private final String productId;
    private final BigDecimal actualQuantity;
    private final String warehouseId;

    public WorkOrderCompletedEvent(String workOrderId, String productId, BigDecimal actualQuantity,
                                   String warehouseId, String tenantId) {
        super("WORK_ORDER_COMPLETED", tenantId, workOrderId, "WorkOrder");
        this.workOrderId = workOrderId;
        this.productId = productId;
        this.actualQuantity = actualQuantity;
        this.warehouseId = warehouseId;
    }

    public String getWorkOrderId() { return workOrderId; }
    public String getProductId() { return productId; }
    public BigDecimal getActualQuantity() { return actualQuantity; }
    public String getWarehouseId() { return warehouseId; }
}
