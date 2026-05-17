package com.orvion.manufacturing.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.manufacturing.domain.model.enums.WorkOrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "work_orders", indexes = {
    @Index(name = "idx_wo_tenant_status", columnList = "tenantId, status"),
    @Index(name = "idx_wo_tenant_product", columnList = "tenantId, productId"),
    @Index(name = "idx_wo_order_number", columnList = "tenantId, orderNumber", unique = true)
})
@Getter @Setter @NoArgsConstructor
public class WorkOrder extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 20, nullable = false, unique = true)
    private String orderNumber;

    @Column(length = 50, nullable = false)
    private String productId;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal plannedQuantity;

    @Column(precision = 18, scale = 4)
    private BigDecimal actualQuantity;

    @Column(length = 50)
    private String bomId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private WorkOrderStatus status;

    @Column(nullable = false)
    private Instant plannedStart;

    @Column(nullable = false)
    private Instant plannedEnd;

    @Column
    private Instant actualStart;

    @Column
    private Instant actualEnd;

    @Column(length = 50)
    private String warehouseId;

    public WorkOrder(String tenantId, String orderNumber, String productId, BigDecimal plannedQuantity,
                     String bomId, Instant plannedStart, Instant plannedEnd, String warehouseId) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.orderNumber = orderNumber;
        this.productId = productId;
        this.plannedQuantity = plannedQuantity;
        this.bomId = bomId;
        this.status = WorkOrderStatus.PLANNED;
        this.plannedStart = plannedStart;
        this.plannedEnd = plannedEnd;
        this.warehouseId = warehouseId;
        this.actualQuantity = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
    }

    public void start() {
        if (status != WorkOrderStatus.PLANNED) {
            throw new BusinessException("INVALID_STATUS", "Only PLANNED work orders can be started. Current status: " + status);
        }
        this.status = WorkOrderStatus.IN_PROGRESS;
        this.actualStart = Instant.now();
    }

    public void complete(BigDecimal actualQty) {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new BusinessException("INVALID_STATUS", "Only IN_PROGRESS work orders can be completed. Current status: " + status);
        }
        if (actualQty == null || actualQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_QTY", "Actual quantity must be positive");
        }
        this.status = WorkOrderStatus.COMPLETED;
        this.actualQuantity = actualQty;
        this.actualEnd = Instant.now();
    }

    public void cancel(String reason) {
        if (status == WorkOrderStatus.COMPLETED) {
            throw new BusinessException("INVALID_STATUS", "Cannot cancel a completed work order");
        }
        this.status = WorkOrderStatus.CANCELLED;
        this.actualEnd = Instant.now();
    }

    public void reportProgress(BigDecimal completedQty) {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new BusinessException("INVALID_STATUS", "Only IN_PROGRESS work orders can report progress");
        }
        if (completedQty == null || completedQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("INVALID_QTY", "Completed quantity cannot be negative");
        }
        this.actualQuantity = completedQty;
    }
}
