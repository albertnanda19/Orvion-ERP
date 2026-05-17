package com.orvion.manufacturing.presentation.controller;

import com.orvion.manufacturing.application.dto.response.QualityInspectionResponse;
import com.orvion.manufacturing.application.usecase.QualityInspectionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manufacturing/quality")
@Tag(name = "Quality Control", description = "Quality inspection endpoints")
public class QualityController extends BaseController {
    private final QualityInspectionUseCase inspectionUseCase;

    public QualityController(QualityInspectionUseCase inspectionUseCase) { this.inspectionUseCase = inspectionUseCase; }

    @PostMapping("/inspections")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new quality inspection")
    @PreAuthorize("hasAnyRole('QUALITY_INSPECTOR', 'MANUFACTURING_MANAGER', 'SUPER_ADMIN')")
    public QualityInspectionResponse createInspection(@RequestBody Map<String, Object> body) {
        return inspectionUseCase.createInspection(
            extractTenantId(),
            (String) body.get("workOrderId"),
            (String) body.get("inspectedBy"),
            new BigDecimal(body.getOrDefault("passedQuantity", "0").toString()),
            new BigDecimal(body.getOrDefault("failedQuantity", "0").toString()),
            (String) body.get("defectReasons"));
    }

    @PostMapping("/inspections/{inspectionId}/complete")
    @Operation(summary = "Complete an inspection")
    @PreAuthorize("hasAnyRole('QUALITY_INSPECTOR', 'MANUFACTURING_MANAGER', 'SUPER_ADMIN')")
    public QualityInspectionResponse completeInspection(@PathVariable UUID inspectionId) {
        return inspectionUseCase.completeInspection(extractTenantId(), inspectionId);
    }

    @GetMapping("/inspections/{inspectionId}")
    @Operation(summary = "Get inspection by ID")
    @PreAuthorize("hasAnyRole('QUALITY_INSPECTOR', 'MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public QualityInspectionResponse getInspection(@PathVariable UUID inspectionId) {
        return inspectionUseCase.getInspectionById(extractTenantId(), inspectionId);
    }

    @GetMapping("/inspections")
    @Operation(summary = "List inspections by work order")
    @PreAuthorize("hasAnyRole('QUALITY_INSPECTOR', 'MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<QualityInspectionResponse> getInspections(@RequestParam(required = false) String workOrderId,
                                                          @RequestParam(required = false) String status) {
        if (workOrderId != null) {
            return inspectionUseCase.getInspectionsByWorkOrder(extractTenantId(), workOrderId);
        } else if (status != null) {
            return inspectionUseCase.getInspectionsByStatus(extractTenantId(), status);
        }
        return List.of();
    }
}
