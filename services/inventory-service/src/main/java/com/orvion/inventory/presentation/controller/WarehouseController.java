package com.orvion.inventory.presentation.controller;

import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.inventory.application.dto.response.WarehouseResponse;
import com.orvion.inventory.application.mapper.InventoryMapper;
import com.orvion.inventory.domain.model.Warehouse;
import com.orvion.inventory.domain.model.enums.WarehouseType;
import com.orvion.inventory.domain.repository.WarehouseRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory/warehouses")
@Tag(name = "Warehouses", description = "Warehouse management")
public class WarehouseController extends BaseController {
    private final WarehouseRepository warehouseRepository;
    private final InventoryMapper mapper;

    public WarehouseController(WarehouseRepository warehouseRepository, InventoryMapper mapper) {
        this.warehouseRepository = warehouseRepository;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "List warehouses")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'INVENTORY_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<WarehouseResponse> getWarehouses() {
        return mapper.toWarehouseResponseList(warehouseRepository.findAllByTenantIdAndActiveTrue(extractTenantId()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create warehouse")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'SUPER_ADMIN')")
    public WarehouseResponse createWarehouse(@RequestParam String code, @RequestParam String name,
                                              @RequestParam(defaultValue = "MAIN") String type) {
        Warehouse wh = new Warehouse(extractTenantId(), code, name, WarehouseType.valueOf(type));
        return mapper.toWarehouseResponse(warehouseRepository.save(wh));
    }
}
