package com.orvion.hcm.presentation.controller;

import com.orvion.hcm.application.dto.request.InitiatePayrollRequest;
import com.orvion.hcm.application.dto.response.PayrollResponse;
import com.orvion.hcm.application.usecase.PayrollUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hcm/payroll")
@Tag(name = "Payroll", description = "Payroll management endpoints")
public class PayrollController extends BaseController {
    private final PayrollUseCase payrollUseCase;

    public PayrollController(PayrollUseCase payrollUseCase) { this.payrollUseCase = payrollUseCase; }

    @PostMapping("/initiate")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Initiate and calculate payroll")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'SUPER_ADMIN')")
    public PayrollResponse initiatePayroll(@Valid @RequestBody InitiatePayrollRequest request) {
        return payrollUseCase.initiatePayroll(extractTenantId(), request);
    }

    @PostMapping("/{payrollId}/approve")
    @Operation(summary = "Approve payroll (mark as paid)")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'SUPER_ADMIN')")
    public PayrollResponse approvePayroll(@PathVariable UUID payrollId) {
        return payrollUseCase.approvePayroll(extractTenantId(), payrollId);
    }

    @GetMapping("/by-period")
    @Operation(summary = "Get payroll by period")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<PayrollResponse> getPayrollByPeriod(@RequestParam int year, @RequestParam int month) {
        return payrollUseCase.getPayrollByPeriod(extractTenantId(), year, month);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get payroll by employee")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<PayrollResponse> getPayrollByEmployee(@PathVariable UUID employeeId) {
        return payrollUseCase.getPayrollByEmployee(extractTenantId(), employeeId);
    }
}
