package com.orvion.sales.infrastructure.persistence.jpa;

import com.orvion.sales.domain.model.SalesOrder;
import com.orvion.sales.domain.repository.SalesOrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalesOrderJpaRepository extends JpaRepository<SalesOrder, UUID>, SalesOrderRepository {
    @Override
    Optional<SalesOrder> findByTenantIdAndOrderNumber(String tenantId, String orderNumber);

    @Override
    List<SalesOrder> findAllByTenantId(String tenantId);

    @Override
    List<SalesOrder> findByTenantIdAndStatus(String tenantId, String status);

    @Override
    List<SalesOrder> findByTenantIdAndCustomerId(String tenantId, String customerId);

    @Override
    @Query("SELECT COUNT(o) FROM SalesOrder o WHERE o.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);
}
