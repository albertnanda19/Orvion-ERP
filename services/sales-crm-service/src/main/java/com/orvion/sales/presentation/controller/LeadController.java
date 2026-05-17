package com.orvion.sales.presentation.controller;

import com.orvion.sales.application.dto.request.CreateLeadRequest;
import com.orvion.sales.application.dto.response.LeadResponse;
import com.orvion.sales.application.dto.response.OpportunityResponse;
import com.orvion.sales.application.usecase.LeadUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales/leads")
@Tag(name = "Leads", description = "Lead management endpoints")
public class LeadController extends BaseController {
    private final LeadUseCase leadUseCase;

    public LeadController(LeadUseCase leadUseCase) { this.leadUseCase = leadUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new lead")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN')")
    public LeadResponse createLead(@Valid @RequestBody CreateLeadRequest request) {
        return leadUseCase.createLead(extractTenantId(), request);
    }

    @GetMapping
    @Operation(summary = "List all leads")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<LeadResponse> getLeads() {
        return leadUseCase.getLeads(extractTenantId());
    }

    @GetMapping("/{leadId}")
    @Operation(summary = "Get lead by ID")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public LeadResponse getLead(@PathVariable UUID leadId) {
        return leadUseCase.getLeadById(extractTenantId(), leadId);
    }

    @PostMapping("/{leadId}/qualify")
    @Operation(summary = "Qualify a lead")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN')")
    public LeadResponse qualifyLead(@PathVariable UUID leadId) {
        return leadUseCase.qualifyLead(extractTenantId(), leadId);
    }

    @PostMapping("/{leadId}/disqualify")
    @Operation(summary = "Disqualify a lead")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN')")
    public LeadResponse disqualifyLead(@PathVariable UUID leadId, @RequestParam String reason) {
        return leadUseCase.disqualifyLead(extractTenantId(), leadId, reason);
    }

    @PostMapping("/{leadId}/convert")
    @Operation(summary = "Convert lead to opportunity")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN')")
    public OpportunityResponse convertLead(@PathVariable UUID leadId, @RequestParam String title) {
        return leadUseCase.convertToOpportunity(extractTenantId(), leadId, title);
    }
}
