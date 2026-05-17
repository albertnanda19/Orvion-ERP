package com.orvion.inventory.infrastructure.persistence;

import com.orvion.inventory.domain.model.Warehouse;
import com.orvion.inventory.domain.repository.WarehouseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseJpaRepository extends JpaRepository<Warehouse, UUID>, WarehouseRepository {
    @Override
    Optional<Warehouse> findByTenantIdAndCode(String tenantId, String code);
    @Override
    List<Warehouse> findAllByTenantIdAndActiveTrue(String tenantId);
}
