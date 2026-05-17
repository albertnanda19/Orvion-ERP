package com.orvion.hcm.presentation.controller;

import com.orvion.hcm.application.dto.request.CreateReviewRequest;
import com.orvion.hcm.application.dto.response.PerformanceReviewResponse;
import com.orvion.hcm.application.usecase.PerformanceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hcm/performance")
@Tag(name = "Performance Reviews", description = "Performance review endpoints")
public class PerformanceController extends BaseController {
    private final PerformanceUseCase performanceUseCase;

    public PerformanceController(PerformanceUseCase performanceUseCase) { this.performanceUseCase = performanceUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a performance review")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'SUPER_ADMIN')")
    public PerformanceReviewResponse createReview(@Valid @RequestBody CreateReviewRequest request) {
        return performanceUseCase.createReview(extractTenantId(), request);
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get review by ID")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public PerformanceReviewResponse getReview(@PathVariable UUID reviewId) {
        return performanceUseCase.getReviewById(extractTenantId(), reviewId);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get reviews by employee")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<PerformanceReviewResponse> getEmployeeReviews(@PathVariable UUID employeeId) {
        return performanceUseCase.getEmployeeReviews(extractTenantId(), employeeId);
    }
}
