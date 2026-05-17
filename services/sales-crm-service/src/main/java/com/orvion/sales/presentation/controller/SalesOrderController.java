package com.orvion.sales.presentation.controller;

import com.orvion.sales.application.dto.request.CreateSalesOrderRequest;
import com.orvion.sales.application.dto.response.SalesOrderResponse;
import com.orvion.sales.application.usecase.SalesOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales/orders")
@Tag(name = "Sales Orders", description = "Sales order management endpoints")
public class SalesOrderController extends BaseController {
    private final SalesOrderUseCase salesOrderUseCase;

    public SalesOrderController(SalesOrderUseCase salesOrderUseCase) { this.salesOrderUseCase = salesOrderUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new sales order")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN')")
    public SalesOrderResponse createOrder(@Valid @RequestBody CreateSalesOrderRequest request) {
        return salesOrderUseCase.createOrder(extractTenantId(), request);
    }

    @GetMapping
    @Operation(summary = "List all sales orders")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<SalesOrderResponse> getOrders() {
        return salesOrderUseCase.getOrders(extractTenantId());
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public SalesOrderResponse getOrder(@PathVariable UUID orderId) {
        return salesOrderUseCase.getOrderById(extractTenantId(), orderId);
    }

    @PostMapping("/{orderId}/confirm")
    @Operation(summary = "Confirm an order")
    @PreAuthorize("hasAnyRole('SALES_MANAGER', 'SUPER_ADMIN')")
    public SalesOrderResponse confirmOrder(@PathVariable UUID orderId) {
        return salesOrderUseCase.confirmOrder(extractTenantId(), orderId);
    }

    @PostMapping("/{orderId}/ship")
    @Operation(summary = "Ship an order")
    @PreAuthorize("hasAnyRole('SALES_MANAGER', 'SUPER_ADMIN')")
    public SalesOrderResponse shipOrder(@PathVariable UUID orderId) {
        return salesOrderUseCase.shipOrder(extractTenantId(), orderId);
    }

    @PostMapping("/{orderId}/deliver")
    @Operation(summary = "Deliver an order")
    @PreAuthorize("hasAnyRole('SALES_MANAGER', 'SUPER_ADMIN')")
    public SalesOrderResponse deliverOrder(@PathVariable UUID orderId) {
        return salesOrderUseCase.deliverOrder(extractTenantId(), orderId);
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel an order")
    @PreAuthorize("hasAnyRole('SALES_MANAGER', 'SUPER_ADMIN')")
    public SalesOrderResponse cancelOrder(@PathVariable UUID orderId, @RequestParam String reason) {
        return salesOrderUseCase.cancelOrder(extractTenantId(), orderId, reason);
    }
}
