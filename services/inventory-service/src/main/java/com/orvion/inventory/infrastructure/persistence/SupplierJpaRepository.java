package com.orvion.inventory.infrastructure.persistence;

import com.orvion.inventory.domain.model.Supplier;
import com.orvion.inventory.domain.repository.SupplierRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierJpaRepository extends JpaRepository<Supplier, UUID>, SupplierRepository {
    @Override
    Optional<Supplier> findByTenantIdAndCode(String tenantId, String code);
    @Override
    List<Supplier> findAllByTenantIdAndActiveTrue(String tenantId);
    @Override
    boolean existsByTenantIdAndCode(String tenantId, String code);
}
