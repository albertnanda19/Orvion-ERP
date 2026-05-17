package com.orvion.inventory.infrastructure.persistence;

import com.orvion.inventory.domain.model.PurchaseOrder;
import com.orvion.inventory.domain.repository.PurchaseOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseOrderJpaRepository extends JpaRepository<PurchaseOrder, UUID>, PurchaseOrderRepository {
    @Override
    Optional<PurchaseOrder> findByTenantIdAndPoNumber(String tenantId, String poNumber);
    @Override
    Page<PurchaseOrder> findByTenantId(String tenantId, Pageable pageable);
}
