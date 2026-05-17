package com.orvion.reporting.domain.model;

import com.orvion.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reporting_audit_logs", indexes = {
    @Index(name = "idx_auditlog_tenant_action", columnList = "tenantId, action"),
    @Index(name = "idx_auditlog_timestamp", columnList = "timestamp")
})
@Getter @Setter @NoArgsConstructor
public class ReportAuditLog extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 100, nullable = false)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "service_name", length = 100)
    private String serviceName;

    @Column(name = "trace_id", length = 50)
    private String traceId;

    @Column(nullable = false)
    private Instant timestamp;
}
