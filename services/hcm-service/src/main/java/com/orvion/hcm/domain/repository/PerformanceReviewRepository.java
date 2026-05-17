package com.orvion.hcm.domain.repository;

import com.orvion.hcm.domain.model.PerformanceReview;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PerformanceReviewRepository {
    PerformanceReview save(PerformanceReview review);
    Optional<PerformanceReview> findById(UUID id);
    List<PerformanceReview> findByTenantIdAndEmployeeId(String tenantId, UUID employeeId);
    List<PerformanceReview> findByTenantId(String tenantId);
}
