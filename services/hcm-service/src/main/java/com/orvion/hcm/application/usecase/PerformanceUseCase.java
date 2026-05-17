package com.orvion.hcm.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.hcm.application.dto.request.CreateReviewRequest;
import com.orvion.hcm.application.dto.response.PerformanceReviewResponse;
import com.orvion.hcm.application.mapper.HcmMapper;
import com.orvion.hcm.domain.model.Employee;
import com.orvion.hcm.domain.model.PerformanceReview;
import com.orvion.hcm.domain.repository.EmployeeRepository;
import com.orvion.hcm.domain.repository.PerformanceReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PerformanceUseCase {
    private final PerformanceReviewRepository reviewRepository;
    private final EmployeeRepository employeeRepository;
    private final HcmMapper mapper;

    public PerformanceUseCase(PerformanceReviewRepository reviewRepository,
                              EmployeeRepository employeeRepository,
                              HcmMapper mapper) {
        this.reviewRepository = reviewRepository;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    public PerformanceReviewResponse createReview(String tenantId, CreateReviewRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId().toString()));
        if (!employee.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Employee does not belong to tenant");

        PerformanceReview review = new PerformanceReview(tenantId, request.getEmployeeId(), request.getReviewPeriod());
        review = reviewRepository.save(review);
        return mapper.toPerformanceReviewResponse(review);
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewResponse> getEmployeeReviews(String tenantId, UUID employeeId) {
        return mapper.toPerformanceReviewResponseList(
            reviewRepository.findByTenantIdAndEmployeeId(tenantId, employeeId));
    }

    @Transactional(readOnly = true)
    public PerformanceReviewResponse getReviewById(String tenantId, UUID reviewId) {
        PerformanceReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("PerformanceReview", "id", reviewId.toString()));
        if (!review.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Review does not belong to tenant");
        return mapper.toPerformanceReviewResponse(review);
    }
}
