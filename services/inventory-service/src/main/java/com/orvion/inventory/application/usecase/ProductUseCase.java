package com.orvion.inventory.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.inventory.application.dto.request.CreateProductRequest;
import com.orvion.inventory.application.dto.request.UpdateProductRequest;
import com.orvion.inventory.application.dto.response.ProductResponse;
import com.orvion.inventory.application.mapper.InventoryMapper;
import com.orvion.inventory.domain.model.Product;
import com.orvion.inventory.domain.model.enums.CostingMethod;
import com.orvion.inventory.domain.model.vo.Money;
import com.orvion.inventory.domain.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductUseCase {
    private final ProductRepository productRepository;
    private final InventoryMapper mapper;

    public ProductUseCase(ProductRepository productRepository, InventoryMapper mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse createProduct(String tenantId, CreateProductRequest request) {
        if (productRepository.existsByTenantIdAndSku(tenantId, request.getSku()))
            throw new BusinessException("DUPLICATE_SKU", "Product with SKU " + request.getSku() + " already exists");
        CostingMethod cm = request.getCostingMethod() != null
            ? CostingMethod.valueOf(request.getCostingMethod())
            : CostingMethod.AVERAGE_COST;
        Product product = new Product(tenantId, request.getSku(), request.getName(), request.getUnit(), cm);
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setReorderPoint(request.getReorderPoint() != null ? request.getReorderPoint() : BigDecimal.ZERO);
        product.setReorderQuantity(request.getReorderQuantity() != null ? request.getReorderQuantity() : BigDecimal.ZERO);
        product.setPreferredSupplierId(request.getPreferredSupplierId());
        product.setWarehouseId(request.getWarehouseId());
        if (request.getStandardCost() != null) {
            product.setStandardCost(new Money(request.getStandardCost(),
                request.getCostCurrency() != null ? request.getCostCurrency() : "IDR"));
        }
        product = productRepository.save(product);
        return mapper.toProductResponse(product);
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateProduct(String tenantId, UUID productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));
        if (!product.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Product does not belong to tenant");
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getReorderPoint() != null) product.setReorderPoint(request.getReorderPoint());
        if (request.getReorderQuantity() != null) product.setReorderQuantity(request.getReorderQuantity());
        if (request.getPreferredSupplierId() != null) product.setPreferredSupplierId(request.getPreferredSupplierId());
        if (request.getWarehouseId() != null) product.setWarehouseId(request.getWarehouseId());
        product = productRepository.save(product);
        return mapper.toProductResponse(product);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deactivateProduct(String tenantId, UUID productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));
        if (!product.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Product does not belong to tenant");
        product.setActive(false);
        productRepository.save(product);
    }

    @Cacheable(value = "products", key = "#tenantId + ':' + #productId")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(String tenantId, UUID productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));
        if (!product.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Product does not belong to tenant");
        return mapper.toProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts(String tenantId) {
        return mapper.toProductResponseList(productRepository.findAllByTenantIdAndActiveTrue(tenantId));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts(String tenantId) {
        return mapper.toProductResponseList(productRepository.findLowStockProducts(tenantId));
    }
}
