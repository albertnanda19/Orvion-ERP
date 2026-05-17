package com.orvion.manufacturing.presentation.controller;

import com.orvion.manufacturing.application.dto.response.MachineResponse;
import com.orvion.manufacturing.application.usecase.MachineUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manufacturing/machines")
@Tag(name = "Machines", description = "Machine management endpoints")
public class MachineController extends BaseController {
    private final MachineUseCase machineUseCase;

    public MachineController(MachineUseCase machineUseCase) { this.machineUseCase = machineUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new machine")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'SUPER_ADMIN')")
    public MachineResponse registerMachine(@RequestBody Map<String, String> body) {
        return machineUseCase.registerMachine(extractTenantId(),
            body.get("machineId"), body.get("name"), body.get("type"));
    }

    @PostMapping("/{machineId}/start")
    @Operation(summary = "Start a machine")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN')")
    public MachineResponse startMachine(@PathVariable UUID machineId) {
        return machineUseCase.startMachine(extractTenantId(), machineId);
    }

    @PostMapping("/{machineId}/stop")
    @Operation(summary = "Stop a machine")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN')")
    public MachineResponse stopMachine(@PathVariable UUID machineId) {
        return machineUseCase.stopMachine(extractTenantId(), machineId);
    }

    @PostMapping("/{machineId}/maintenance")
    @Operation(summary = "Mark machine for maintenance")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'SUPER_ADMIN')")
    public MachineResponse markMaintenance(@PathVariable UUID machineId) {
        return machineUseCase.markMaintenance(extractTenantId(), machineId);
    }

    @PostMapping("/{machineId}/maintenance/complete")
    @Operation(summary = "Complete maintenance")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'SUPER_ADMIN')")
    public MachineResponse completeMaintenance(@PathVariable UUID machineId, @RequestBody Map<String, String> body) {
        Instant nextMaint = body.containsKey("nextMaintenanceDate") ?
            Instant.parse(body.get("nextMaintenanceDate")) : Instant.now().plusSeconds(30 * 24 * 3600);
        return machineUseCase.completeMaintenance(extractTenantId(), machineId, nextMaint);
    }

    @PostMapping("/{machineId}/breakdown")
    @Operation(summary = "Mark machine breakdown")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'SUPER_ADMIN')")
    public MachineResponse markBreakdown(@PathVariable UUID machineId) {
        return machineUseCase.markBreakdown(extractTenantId(), machineId);
    }

    @PutMapping("/{machineId}/oee-target")
    @Operation(summary = "Update OEE target")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'SUPER_ADMIN')")
    public MachineResponse updateOeeTarget(@PathVariable UUID machineId, @RequestBody Map<String, BigDecimal> body) {
        return machineUseCase.updateOeeTarget(extractTenantId(), machineId, body.get("oeeTarget"));
    }

    @GetMapping("/{machineId}")
    @Operation(summary = "Get machine by ID")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public MachineResponse getMachine(@PathVariable UUID machineId) {
        return machineUseCase.getMachineById(extractTenantId(), machineId);
    }

    @GetMapping
    @Operation(summary = "List all machines")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<MachineResponse> getMachines() {
        return machineUseCase.getMachines(extractTenantId());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get machines by status")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<MachineResponse> getMachinesByStatus(@PathVariable String status) {
        return machineUseCase.getMachinesByStatus(extractTenantId(), status);
    }
}
