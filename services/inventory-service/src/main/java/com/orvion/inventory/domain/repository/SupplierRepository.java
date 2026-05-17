package com.orvion.inventory.domain.repository;
import com.orvion.inventory.domain.model.Supplier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository {
    Supplier save(Supplier supplier);
    Optional<Supplier> findById(UUID id);
    Optional<Supplier> findByTenantIdAndCode(String tenantId, String code);
    List<Supplier> findAllByTenantIdAndActiveTrue(String tenantId);
    boolean existsByTenantIdAndCode(String tenantId, String code);
}
