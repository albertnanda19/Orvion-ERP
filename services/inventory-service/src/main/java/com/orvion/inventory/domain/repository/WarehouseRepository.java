package com.orvion.inventory.domain.repository;
import com.orvion.inventory.domain.model.Warehouse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository {
    Warehouse save(Warehouse warehouse);
    Optional<Warehouse> findById(UUID id);
    Optional<Warehouse> findByTenantIdAndCode(String tenantId, String code);
    List<Warehouse> findAllByTenantIdAndActiveTrue(String tenantId);
}
