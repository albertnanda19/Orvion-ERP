package com.orvion.hcm.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PerformanceReviewResponse {
    private UUID id;
    private UUID employeeId;
    private String reviewPeriod;
    private BigDecimal overallScore;
    private String reviewedBy;
    private String status;
    private Instant createdAt;
}
