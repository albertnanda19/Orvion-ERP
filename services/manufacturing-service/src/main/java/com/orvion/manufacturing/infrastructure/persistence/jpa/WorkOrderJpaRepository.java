package com.orvion.manufacturing.infrastructure.persistence.jpa;

import com.orvion.manufacturing.domain.model.WorkOrder;
import com.orvion.manufacturing.domain.model.enums.WorkOrderStatus;
import com.orvion.manufacturing.domain.repository.WorkOrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkOrderJpaRepository extends JpaRepository<WorkOrder, UUID>, WorkOrderRepository {
    @Override
    Optional<WorkOrder> findByTenantIdAndOrderNumber(String tenantId, String orderNumber);

    @Override
    List<WorkOrder> findAllByTenantId(String tenantId);

    @Override
    List<WorkOrder> findByTenantIdAndStatus(String tenantId, WorkOrderStatus status);

    @Override
    List<WorkOrder> findByTenantIdAndPlannedStartBetween(String tenantId, Instant from, Instant to);

    @Override
    boolean existsByTenantIdAndOrderNumber(String tenantId, String orderNumber);
}
