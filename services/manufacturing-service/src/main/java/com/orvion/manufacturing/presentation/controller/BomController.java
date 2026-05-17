package com.orvion.manufacturing.presentation.controller;

import com.orvion.manufacturing.application.dto.request.CreateBomRequest;
import com.orvion.manufacturing.application.dto.response.BomResponse;
import com.orvion.manufacturing.application.usecase.BomUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manufacturing/bom")
@Tag(name = "Bill of Materials", description = "BOM management endpoints")
public class BomController extends BaseController {
    private final BomUseCase bomUseCase;

    public BomController(BomUseCase bomUseCase) { this.bomUseCase = bomUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new BOM")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'SUPER_ADMIN')")
    public BomResponse createBom(@Valid @RequestBody CreateBomRequest request) {
        return bomUseCase.createBom(extractTenantId(), request);
    }

    @GetMapping("/{bomId}")
    @Operation(summary = "Get BOM by ID")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN')")
    public BomResponse getBom(@PathVariable UUID bomId) {
        return bomUseCase.getBomById(extractTenantId(), bomId);
    }

    @GetMapping
    @Operation(summary = "List all active BOMs")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN')")
    public List<BomResponse> getActiveBoms() {
        return bomUseCase.getActiveBoms(extractTenantId());
    }

    @DeleteMapping("/{bomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate a BOM")
    @PreAuthorize("hasAnyRole('MANUFACTURING_MANAGER', 'SUPER_ADMIN')")
    public void deactivateBom(@PathVariable UUID bomId) {
        bomUseCase.deactivateBom(extractTenantId(), bomId);
    }
}
