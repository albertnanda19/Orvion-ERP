package com.orvion.manufacturing.domain.repository;

import com.orvion.manufacturing.domain.model.WorkOrder;
import com.orvion.manufacturing.domain.model.enums.WorkOrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkOrderRepository {
    WorkOrder save(WorkOrder workOrder);
    Optional<WorkOrder> findById(UUID id);
    Optional<WorkOrder> findByTenantIdAndOrderNumber(String tenantId, String orderNumber);
    List<WorkOrder> findAllByTenantId(String tenantId);
    List<WorkOrder> findByTenantIdAndStatus(String tenantId, WorkOrderStatus status);
    List<WorkOrder> findByTenantIdAndPlannedStartBetween(String tenantId, Instant from, Instant to);
    boolean existsByTenantIdAndOrderNumber(String tenantId, String orderNumber);
    void delete(WorkOrder workOrder);
}
