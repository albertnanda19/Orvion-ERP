package com.orvion.inventory.presentation.controller;

import com.orvion.inventory.application.dto.request.CreateGoodsReceiptRequest;
import com.orvion.inventory.application.dto.response.GoodsReceiptResponse;
import com.orvion.inventory.application.usecase.GoodsReceiptUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory/goods-receipts")
@Tag(name = "Goods Receipts", description = "Goods receipt management")
public class GoodsReceiptController extends BaseController {
    private final GoodsReceiptUseCase goodsReceiptUseCase;

    public GoodsReceiptController(GoodsReceiptUseCase goodsReceiptUseCase) { this.goodsReceiptUseCase = goodsReceiptUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create goods receipt")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'INVENTORY_STAFF', 'SUPER_ADMIN')")
    public GoodsReceiptResponse createGoodsReceipt(@Valid @RequestBody CreateGoodsReceiptRequest request) {
        return goodsReceiptUseCase.createGoodsReceipt(extractTenantId(), request);
    }

    @PostMapping("/{grId}/confirm")
    @Operation(summary = "Confirm goods receipt")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'SUPER_ADMIN')")
    public GoodsReceiptResponse confirmGoodsReceipt(@PathVariable UUID grId) {
        return goodsReceiptUseCase.confirmGoodsReceipt(extractTenantId(), grId);
    }
}
