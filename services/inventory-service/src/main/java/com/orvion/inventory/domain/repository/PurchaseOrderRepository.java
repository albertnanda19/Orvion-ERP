package com.orvion.inventory.domain.repository;
import com.orvion.inventory.domain.model.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderRepository {
    PurchaseOrder save(PurchaseOrder purchaseOrder);
    Optional<PurchaseOrder> findById(UUID id);
    Optional<PurchaseOrder> findByTenantIdAndPoNumber(String tenantId, String poNumber);
    Page<PurchaseOrder> findByTenantId(String tenantId, Pageable pageable);
}
