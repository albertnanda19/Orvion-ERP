package com.orvion.inventory.presentation.controller;

import com.orvion.inventory.application.dto.request.CreateSupplierRequest;
import com.orvion.inventory.application.dto.response.SupplierResponse;
import com.orvion.inventory.application.usecase.SupplierUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory/suppliers")
@Tag(name = "Suppliers", description = "Supplier management")
public class SupplierController extends BaseController {
    private final SupplierUseCase supplierUseCase;

    public SupplierController(SupplierUseCase supplierUseCase) { this.supplierUseCase = supplierUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create supplier")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'SUPER_ADMIN')")
    public SupplierResponse createSupplier(@Valid @RequestBody CreateSupplierRequest request) {
        return supplierUseCase.createSupplier(extractTenantId(), request);
    }

    @PutMapping("/{supplierId}")
    @Operation(summary = "Update supplier")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'SUPER_ADMIN')")
    public SupplierResponse updateSupplier(@PathVariable UUID supplierId, @Valid @RequestBody CreateSupplierRequest request) {
        return supplierUseCase.updateSupplier(extractTenantId(), supplierId, request);
    }

    @GetMapping
    @Operation(summary = "List suppliers")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'INVENTORY_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<SupplierResponse> getSuppliers() {
        return supplierUseCase.getSuppliers(extractTenantId());
    }
}
