package com.orvion.hcm.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.hcm.domain.model.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "performance_reviews", indexes = {
    @Index(name = "idx_pr_tenant_employee", columnList = "tenantId, employeeId")
})
@Getter @Setter @NoArgsConstructor
public class PerformanceReview extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private UUID employeeId;

    @Column(length = 20, nullable = false)
    private String reviewPeriod;

    @Column(columnDefinition = "JSONB")
    private String goalsJson;

    @Transient
    private List<Goal> goals = new ArrayList<>();

    @Column(precision = 3, scale = 1)
    private BigDecimal overallScore;

    @Column(length = 100)
    private String reviewedBy;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ReviewStatus status = ReviewStatus.DRAFT;

    public PerformanceReview(String tenantId, UUID employeeId, String reviewPeriod) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.employeeId = employeeId;
        this.reviewPeriod = reviewPeriod;
        this.status = ReviewStatus.DRAFT;
    }

    @Getter @Setter @NoArgsConstructor
    public static class Goal {
        private String description;
        private BigDecimal weight;
        private BigDecimal score;
        private String comment;

        public Goal(String description, BigDecimal weight, BigDecimal score, String comment) {
            this.description = description;
            this.weight = weight;
            this.score = score;
            this.comment = comment;
        }
    }
}
