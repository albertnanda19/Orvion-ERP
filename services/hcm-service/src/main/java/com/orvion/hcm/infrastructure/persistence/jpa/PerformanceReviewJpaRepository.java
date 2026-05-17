package com.orvion.hcm.infrastructure.persistence.jpa;

import com.orvion.hcm.domain.model.PerformanceReview;
import com.orvion.hcm.domain.repository.PerformanceReviewRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PerformanceReviewJpaRepository extends JpaRepository<PerformanceReview, UUID>, PerformanceReviewRepository {
    @Override
    List<PerformanceReview> findByTenantIdAndEmployeeId(String tenantId, UUID employeeId);

    @Override
    List<PerformanceReview> findByTenantId(String tenantId);
}
