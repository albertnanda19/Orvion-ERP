package com.orvion.reporting.domain.model;

import com.orvion.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "reporting_hcm_facts", indexes = {
    @Index(name = "idx_hcm_fact_tenant_period", columnList = "tenantId, period")
})
@Getter @Setter @NoArgsConstructor
public class ReportHcmFact extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 10, nullable = false)
    private String period;

    @Column(name = "total_employees")
    private Long totalEmployees;

    @Column(name = "total_payroll", precision = 18, scale = 2)
    private BigDecimal totalPayroll;

    @Column(name = "dept_counts", columnDefinition = "TEXT")
    private String deptCounts;
}
