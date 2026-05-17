package com.orvion.finance.presentation.controller;

import com.orvion.finance.application.dto.response.*;
import com.orvion.finance.application.usecase.FinancialReportUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/finance/reports")
@Tag(name = "Reports", description = "Financial reports and dashboard")
public class ReportController {

    private final FinancialReportUseCase financialReportUseCase;

    public ReportController(FinancialReportUseCase financialReportUseCase) {
        this.financialReportUseCase = financialReportUseCase;
    }

    @GetMapping("/trial-balance")
    @Operation(summary = "Generate trial balance")
    @PreAuthorize("hasAnyRole('FINANCE_MANAGER', 'FINANCE_STAFF', 'REPORT_VIEWER', 'SUPER_ADMIN')")
    public ResponseEntity<TrialBalanceResponse> getTrialBalance(
            @RequestParam int year, @RequestParam int month,
            HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        TrialBalanceResponse response = financialReportUseCase.generateTrialBalance(tenantId, year, month);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profit-and-loss")
    @Operation(summary = "Generate profit and loss statement")
    @PreAuthorize("hasAnyRole('FINANCE_MANAGER', 'FINANCE_STAFF', 'REPORT_VIEWER', 'SUPER_ADMIN')")
    public ResponseEntity<ProfitLossResponse> getProfitAndLoss(
            @RequestParam int startYear, @RequestParam int startMonth,
            @RequestParam int endYear, @RequestParam int endMonth,
            HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        ProfitLossResponse response = financialReportUseCase.generateProfitAndLoss(
            tenantId, startYear, startMonth, endYear, endMonth);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance-sheet")
    @Operation(summary = "Generate balance sheet")
    @PreAuthorize("hasAnyRole('FINANCE_MANAGER', 'FINANCE_STAFF', 'REPORT_VIEWER', 'SUPER_ADMIN')")
    public ResponseEntity<BalanceSheetResponse> getBalanceSheet(
            @RequestParam(required = false) Instant asOfDate,
            HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        if (asOfDate == null) asOfDate = Instant.now();
        BalanceSheetResponse response = financialReportUseCase.generateBalanceSheet(tenantId, asOfDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get finance dashboard summary")
    @PreAuthorize("hasAnyRole('FINANCE_MANAGER', 'FINANCE_STAFF', 'REPORT_VIEWER', 'SUPER_ADMIN')")
    public ResponseEntity<FinanceDashboardResponse> getDashboard(HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        FinanceDashboardResponse response = financialReportUseCase.getDashboardSummary(tenantId);
        return ResponseEntity.ok(response);
    }

    private String extractTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader("X-Tenant-Id");
        if (tenantId == null || tenantId.isBlank()) {
            throw new com.orvion.common.exception.UnauthorizedException("Missing X-Tenant-Id header");
        }
        return tenantId;
    }
}
