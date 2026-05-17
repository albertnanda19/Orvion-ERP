package com.orvion.inventory.presentation.controller;

import com.orvion.inventory.application.dto.request.CreatePurchaseOrderRequest;
import com.orvion.inventory.application.dto.response.PurchaseOrderResponse;
import com.orvion.inventory.application.usecase.PurchaseOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory/purchase-orders")
@Tag(name = "Purchase Orders", description = "Purchase order management")
public class PurchaseOrderController extends BaseController {
    private final PurchaseOrderUseCase purchaseOrderUseCase;

    public PurchaseOrderController(PurchaseOrderUseCase purchaseOrderUseCase) { this.purchaseOrderUseCase = purchaseOrderUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create purchase order")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'SUPER_ADMIN')")
    public PurchaseOrderResponse createPurchaseOrder(@Valid @RequestBody CreatePurchaseOrderRequest request) {
        return purchaseOrderUseCase.createPurchaseOrder(extractTenantId(), extractUserId(), request);
    }

    @PostMapping("/{poId}/approve")
    @Operation(summary = "Approve purchase order")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'SUPER_ADMIN')")
    public PurchaseOrderResponse approvePurchaseOrder(@PathVariable UUID poId) {
        return purchaseOrderUseCase.approvePurchaseOrder(extractTenantId(), extractUserId(), poId);
    }

    @PostMapping("/{poId}/cancel")
    @Operation(summary = "Cancel purchase order")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'SUPER_ADMIN')")
    public PurchaseOrderResponse cancelPurchaseOrder(@PathVariable UUID poId, @RequestParam String reason) {
        return purchaseOrderUseCase.cancelPurchaseOrder(extractTenantId(), poId, reason);
    }

    @GetMapping("/{poId}")
    @Operation(summary = "Get purchase order")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'INVENTORY_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public PurchaseOrderResponse getPurchaseOrder(@PathVariable UUID poId) {
        return purchaseOrderUseCase.getPurchaseOrder(extractTenantId(), poId);
    }

    @GetMapping
    @Operation(summary = "List purchase orders")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'INVENTORY_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public Page<PurchaseOrderResponse> getPurchaseOrders(Pageable pageable) {
        return purchaseOrderUseCase.getActivePurchaseOrders(extractTenantId(), pageable);
    }
}
