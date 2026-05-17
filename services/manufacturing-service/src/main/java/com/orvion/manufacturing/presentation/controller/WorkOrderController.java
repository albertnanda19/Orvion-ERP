package com.orvion.manufacturing.presentation.controller;

import com.orvion.manufacturing.application.dto.request.CreateWorkOrderRequest;
import com.orvion.manufacturing.application.dto.response.WorkOrderResponse;
import com.orvion.manufacturing.application.usecase.WorkOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manufacturing/work-orders")
@Tag(name = "Work Orders", description = "Work order management endpoints")
public class WorkOrderController extends BaseController {
    private final WorkOrderUseCase workOrderUseCase;

    public WorkOrderController(WorkOrderUseCase workOrderUseCase) { this.workOrderUseCase = workOrderUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new work order")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'SUPER_ADMIN')")
    public WorkOrderResponse createWorkOrder(@Valid @RequestBody CreateWorkOrderRequest request) {
        return workOrderUseCase.createWorkOrder(extractTenantId(), request);
    }

    @PostMapping("/{workOrderId}/start")
    @Operation(summary = "Start a work order")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'SUPER_ADMIN')")
    public WorkOrderResponse startWorkOrder(@PathVariable UUID workOrderId) {
        return workOrderUseCase.startWorkOrder(extractTenantId(), workOrderId);
    }

    @PostMapping("/{workOrderId}/complete")
    @Operation(summary = "Complete a work order")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'SUPER_ADMIN')")
    public WorkOrderResponse completeWorkOrder(@PathVariable UUID workOrderId, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal actualQty = body.getOrDefault("actualQuantity", BigDecimal.ZERO);
        return workOrderUseCase.completeWorkOrder(extractTenantId(), workOrderId, actualQty);
    }

    @PostMapping("/{workOrderId}/cancel")
    @Operation(summary = "Cancel a work order")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'SUPER_ADMIN')")
    public WorkOrderResponse cancelWorkOrder(@PathVariable UUID workOrderId, @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "No reason provided");
        return workOrderUseCase.cancelWorkOrder(extractTenantId(), workOrderId, reason);
    }

    @PostMapping("/{workOrderId}/progress")
    @Operation(summary = "Report progress on a work order")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN')")
    public WorkOrderResponse reportProgress(@PathVariable UUID workOrderId, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal completedQty = body.getOrDefault("completedQuantity", BigDecimal.ZERO);
        return workOrderUseCase.reportProgress(extractTenantId(), workOrderId, completedQty);
    }

    @GetMapping("/{workOrderId}")
    @Operation(summary = "Get work order by ID")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public WorkOrderResponse getWorkOrder(@PathVariable UUID workOrderId) {
        return workOrderUseCase.getWorkOrderById(extractTenantId(), workOrderId);
    }

    @GetMapping
    @Operation(summary = "List all work orders")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<WorkOrderResponse> getWorkOrders() {
        return workOrderUseCase.getWorkOrders(extractTenantId());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get work orders by status")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<WorkOrderResponse> getWorkOrdersByStatus(@PathVariable String status) {
        return workOrderUseCase.getWorkOrdersByStatus(extractTenantId(), status);
    }
}
