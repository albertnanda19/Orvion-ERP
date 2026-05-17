package com.orvion.sales.presentation.controller;

import com.orvion.sales.application.dto.request.CreateOpportunityRequest;
import com.orvion.sales.application.dto.response.OpportunityResponse;
import com.orvion.sales.application.usecase.OpportunityUseCase;
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
@RequestMapping("/api/v1/sales/opportunities")
@Tag(name = "Opportunities", description = "Opportunity management endpoints")
public class OpportunityController extends BaseController {
    private final OpportunityUseCase opportunityUseCase;

    public OpportunityController(OpportunityUseCase opportunityUseCase) { this.opportunityUseCase = opportunityUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new opportunity")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN')")
    public OpportunityResponse createOpportunity(@Valid @RequestBody CreateOpportunityRequest request) {
        return opportunityUseCase.createOpportunity(extractTenantId(), request);
    }

    @GetMapping
    @Operation(summary = "List all opportunities")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<OpportunityResponse> getOpportunities() {
        return opportunityUseCase.getOpportunities(extractTenantId());
    }

    @GetMapping("/{oppId}")
    @Operation(summary = "Get opportunity by ID")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public OpportunityResponse getOpportunity(@PathVariable UUID oppId) {
        return opportunityUseCase.getOpportunityById(extractTenantId(), oppId);
    }

    @PostMapping("/{oppId}/advance")
    @Operation(summary = "Advance opportunity stage")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN')")
    public OpportunityResponse advanceStage(@PathVariable UUID oppId) {
        return opportunityUseCase.advanceStage(extractTenantId(), oppId);
    }

    @PostMapping("/{oppId}/close-won")
    @Operation(summary = "Close opportunity as won")
    @PreAuthorize("hasAnyRole('SALES_MANAGER', 'SUPER_ADMIN')")
    public OpportunityResponse closeWon(@PathVariable UUID oppId,
                                         @RequestParam(required = false) BigDecimal actualValue,
                                         @RequestParam(required = false) String currency) {
        return opportunityUseCase.closeWon(extractTenantId(), oppId, actualValue, currency);
    }

    @PostMapping("/{oppId}/close-lost")
    @Operation(summary = "Close opportunity as lost")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN')")
    public OpportunityResponse closeLost(@PathVariable UUID oppId, @RequestParam String reason) {
        return opportunityUseCase.closeLost(extractTenantId(), oppId, reason);
    }
}
