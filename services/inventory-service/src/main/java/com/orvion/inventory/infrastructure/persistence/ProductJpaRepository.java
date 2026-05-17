package com.orvion.inventory.infrastructure.persistence;

import com.orvion.inventory.domain.model.Product;
import com.orvion.inventory.domain.repository.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, UUID>, ProductRepository {
    @Override
    Optional<Product> findByTenantIdAndSku(String tenantId, String sku);
    
    @Override
    List<Product> findAllByTenantIdAndActiveTrue(String tenantId);
    
    @Override
    @Query("SELECT p FROM Product p WHERE p.tenantId = :tenantId AND p.active = true AND (p.currentStock - p.reservedStock) <= p.reorderPoint")
    List<Product> findLowStockProducts(@Param("tenantId") String tenantId);
    
    @Override
    boolean existsByTenantIdAndSku(String tenantId, String sku);
}
