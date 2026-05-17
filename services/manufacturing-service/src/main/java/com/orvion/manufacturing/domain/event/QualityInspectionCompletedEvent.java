package com.orvion.manufacturing.domain.event;

import com.orvion.common.event.DomainEvent;

import java.math.BigDecimal;

public class QualityInspectionCompletedEvent extends DomainEvent {
    private final String inspectionId;
    private final String workOrderId;
    private final String status;
    private final BigDecimal passedQuantity;
    private final BigDecimal failedQuantity;

    public QualityInspectionCompletedEvent(String inspectionId, String workOrderId, String status,
                                           BigDecimal passedQuantity, BigDecimal failedQuantity, String tenantId) {
        super("QUALITY_INSPECTION_COMPLETED", tenantId, inspectionId, "QualityInspection");
        this.inspectionId = inspectionId;
        this.workOrderId = workOrderId;
        this.status = status;
        this.passedQuantity = passedQuantity;
        this.failedQuantity = failedQuantity;
    }

    public String getInspectionId() { return inspectionId; }
    public String getWorkOrderId() { return workOrderId; }
    public String getStatus() { return status; }
    public BigDecimal getPassedQuantity() { return passedQuantity; }
    public BigDecimal getFailedQuantity() { return failedQuantity; }
}
