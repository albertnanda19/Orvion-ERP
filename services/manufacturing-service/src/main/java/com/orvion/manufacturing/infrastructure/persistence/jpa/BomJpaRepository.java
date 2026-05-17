package com.orvion.manufacturing.infrastructure.persistence.jpa;

import com.orvion.manufacturing.domain.model.BillOfMaterials;
import com.orvion.manufacturing.domain.repository.BomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BomJpaRepository extends JpaRepository<BillOfMaterials, UUID>, BomRepository {
    @Override
    List<BillOfMaterials> findAllByTenantIdAndActiveTrue(String tenantId);

    @Override
    Optional<BillOfMaterials> findByTenantIdAndProductIdAndActiveTrue(String tenantId, String productId);

    @Override
    boolean existsByTenantIdAndProductId(String tenantId, String productId);
}
