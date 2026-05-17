package com.orvion.inventory.domain.repository;
import com.orvion.inventory.domain.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    Optional<Product> findByTenantIdAndSku(String tenantId, String sku);
    List<Product> findAllByTenantIdAndActiveTrue(String tenantId);
    List<Product> findLowStockProducts(String tenantId);
    boolean existsByTenantIdAndSku(String tenantId, String sku);
    void delete(Product product);
}
