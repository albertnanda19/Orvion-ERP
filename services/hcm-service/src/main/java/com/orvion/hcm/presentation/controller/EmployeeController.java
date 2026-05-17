package com.orvion.hcm.presentation.controller;

import com.orvion.hcm.application.dto.request.CreateEmployeeRequest;
import com.orvion.hcm.application.dto.request.UpdateEmployeeRequest;
import com.orvion.hcm.application.dto.response.EmployeeResponse;
import com.orvion.hcm.application.usecase.EmployeeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hcm/employees")
@Tag(name = "Employees", description = "Employee management endpoints")
public class EmployeeController extends BaseController {
    private final EmployeeUseCase employeeUseCase;

    public EmployeeController(EmployeeUseCase employeeUseCase) { this.employeeUseCase = employeeUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new employee")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'SUPER_ADMIN')")
    public EmployeeResponse createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return employeeUseCase.createEmployee(extractTenantId(), request);
    }

    @PutMapping("/{employeeId}")
    @Operation(summary = "Update an employee")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'SUPER_ADMIN')")
    public EmployeeResponse updateEmployee(@PathVariable UUID employeeId, @Valid @RequestBody UpdateEmployeeRequest request) {
        return employeeUseCase.updateEmployee(extractTenantId(), employeeId, request);
    }

    @PostMapping("/{employeeId}/terminate")
    @Operation(summary = "Terminate an employee")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'SUPER_ADMIN')")
    public void terminateEmployee(@PathVariable UUID employeeId, @RequestParam String reason) {
        employeeUseCase.terminateEmployee(extractTenantId(), employeeId, reason);
    }

    @PostMapping("/{employeeId}/promote")
    @Operation(summary = "Promote an employee")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'SUPER_ADMIN')")
    public EmployeeResponse promoteEmployee(@PathVariable UUID employeeId, @RequestParam String newPosition,
                                             @RequestParam(required = false) BigDecimal newSalary,
                                             @RequestParam(required = false) String currency) {
        employeeUseCase.promoteEmployee(extractTenantId(), employeeId, newPosition, newSalary, currency);
        return employeeUseCase.getEmployeeById(extractTenantId(), employeeId);
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Get employee by ID")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public EmployeeResponse getEmployee(@PathVariable UUID employeeId) {
        return employeeUseCase.getEmployeeById(extractTenantId(), employeeId);
    }

    @GetMapping
    @Operation(summary = "List all active employees")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<EmployeeResponse> getEmployees() {
        return employeeUseCase.getEmployees(extractTenantId());
    }

    @GetMapping("/by-department")
    @Operation(summary = "Get employees by department")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<EmployeeResponse> getEmployeesByDepartment(@RequestParam String department) {
        return employeeUseCase.getEmployeesByDepartment(extractTenantId(), department);
    }
}
