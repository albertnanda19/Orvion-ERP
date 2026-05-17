package com.orvion.inventory.presentation.controller;

import com.orvion.inventory.application.dto.request.AdjustStockRequest;
import com.orvion.inventory.application.dto.request.IssueStockRequest;
import com.orvion.inventory.application.dto.request.ReceiveStockRequest;
import com.orvion.inventory.application.dto.response.StockMovementResponse;
import com.orvion.inventory.application.usecase.StockMovementUseCase;
import com.orvion.inventory.domain.model.vo.Money;
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
@RequestMapping("/api/v1/inventory/stock-movements")
@Tag(name = "Stock Movements", description = "Stock movement management")
public class StockMovementController extends BaseController {
    private final StockMovementUseCase stockMovementUseCase;

    public StockMovementController(StockMovementUseCase stockMovementUseCase) { this.stockMovementUseCase = stockMovementUseCase; }

    @PostMapping("/receive")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Receive stock")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'INVENTORY_STAFF', 'SUPER_ADMIN')")
    public StockMovementResponse receiveStock(@Valid @RequestBody ReceiveStockRequest request) {
        return stockMovementUseCase.receiveStock(extractTenantId(), UUID.fromString(request.getProductId()),
            UUID.fromString(request.getWarehouseId()), request.getQuantity(),
            new Money(request.getUnitCost(), request.getCurrency() != null ? request.getCurrency() : "IDR"),
            request.getReference(), request.getSourceDocument(), extractUserId());
    }

    @PostMapping("/issue")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Issue stock")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'INVENTORY_STAFF', 'SUPER_ADMIN')")
    public StockMovementResponse issueStock(@Valid @RequestBody IssueStockRequest request) {
        return stockMovementUseCase.issueStock(extractTenantId(), UUID.fromString(request.getProductId()),
            UUID.fromString(request.getWarehouseId()), request.getQuantity(),
            request.getReference(), request.getSourceDocument(), extractUserId());
    }

    @PostMapping("/adjust")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adjust stock")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'SUPER_ADMIN')")
    public StockMovementResponse adjustStock(@Valid @RequestBody AdjustStockRequest request) {
        return stockMovementUseCase.adjustStock(extractTenantId(), UUID.fromString(request.getProductId()),
            UUID.fromString(request.getWarehouseId()), request.getAdjustment(),
            request.getReason(), extractUserId());
    }

    @GetMapping
    @Operation(summary = "Get movement history")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'INVENTORY_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public Page<StockMovementResponse> getMovements(@RequestParam UUID productId, Pageable pageable) {
        return stockMovementUseCase.getMovementHistory(extractTenantId(), productId, pageable);
    }
}
