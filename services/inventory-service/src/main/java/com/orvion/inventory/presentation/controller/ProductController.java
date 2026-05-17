package com.orvion.inventory.presentation.controller;

import com.orvion.inventory.application.dto.request.CreateProductRequest;
import com.orvion.inventory.application.dto.request.UpdateProductRequest;
import com.orvion.inventory.application.dto.response.ProductResponse;
import com.orvion.inventory.application.usecase.ProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory/products")
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController extends BaseController {
    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) { this.productUseCase = productUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'SUPER_ADMIN')")
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productUseCase.createProduct(extractTenantId(), request);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update a product")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'SUPER_ADMIN')")
    public ProductResponse updateProduct(@PathVariable UUID productId, @Valid @RequestBody UpdateProductRequest request) {
        return productUseCase.updateProduct(extractTenantId(), productId, request);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate a product")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'SUPER_ADMIN')")
    public void deactivateProduct(@PathVariable UUID productId) {
        productUseCase.deactivateProduct(extractTenantId(), productId);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'INVENTORY_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public ProductResponse getProduct(@PathVariable UUID productId) {
        return productUseCase.getProductById(extractTenantId(), productId);
    }

    @GetMapping
    @Operation(summary = "List all active products")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'INVENTORY_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<ProductResponse> getProducts() {
        return productUseCase.getProducts(extractTenantId());
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock products")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'SUPER_ADMIN')")
    public List<ProductResponse> getLowStockProducts() {
        return productUseCase.getLowStockProducts(extractTenantId());
    }
}
