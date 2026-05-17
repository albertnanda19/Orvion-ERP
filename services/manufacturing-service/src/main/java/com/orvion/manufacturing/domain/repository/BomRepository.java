package com.orvion.manufacturing.domain.repository;

import com.orvion.manufacturing.domain.model.BillOfMaterials;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BomRepository {
    BillOfMaterials save(BillOfMaterials bom);
    Optional<BillOfMaterials> findById(UUID id);
    List<BillOfMaterials> findAllByTenantIdAndActiveTrue(String tenantId);
    Optional<BillOfMaterials> findByTenantIdAndProductIdAndActiveTrue(String tenantId, String productId);
    boolean existsByTenantIdAndProductId(String tenantId, String productId);
    void delete(BillOfMaterials bom);
}
