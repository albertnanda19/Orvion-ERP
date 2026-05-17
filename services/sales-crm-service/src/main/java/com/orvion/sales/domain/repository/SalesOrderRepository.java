package com.orvion.sales.domain.repository;

import com.orvion.sales.domain.model.SalesOrder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SalesOrderRepository {
    SalesOrder save(SalesOrder salesOrder);
    Optional<SalesOrder> findById(UUID id);
    Optional<SalesOrder> findByTenantIdAndOrderNumber(String tenantId, String orderNumber);
    List<SalesOrder> findAllByTenantId(String tenantId);
    List<SalesOrder> findByTenantIdAndStatus(String tenantId, String status);
    List<SalesOrder> findByTenantIdAndCustomerId(String tenantId, String customerId);
    long countByTenantId(String tenantId);
    void delete(SalesOrder salesOrder);
}
