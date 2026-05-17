package com.orvion.reporting.domain.model;

import com.orvion.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "report_definitions", indexes = {
    @Index(name = "idx_repdef_tenant_active", columnList = "tenantId, active")
})
@Getter @Setter @NoArgsConstructor
public class ReportDefinition extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "report_type", length = 30, nullable = false)
    private String reportType;

    @Column(name = "query_config", columnDefinition = "TEXT")
    private String queryConfig;

    @Column(name = "schedule_config", length = 100)
    private String scheduleConfig;

    @Column(name = "output_format", length = 10)
    private String outputFormat;

    @Column(nullable = false)
    private boolean active = true;

    public ReportDefinition(String tenantId, String name, String reportType) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.name = name;
        this.reportType = reportType;
        this.active = true;
    }
}
