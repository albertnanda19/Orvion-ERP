package com.orvion.reporting.domain.model;

import com.orvion.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "report_executions", indexes = {
    @Index(name = "idx_repexec_tenant_status", columnList = "tenantId, status"),
    @Index(name = "idx_repexec_def_id", columnList = "reportDefinitionId")
})
@Getter @Setter @NoArgsConstructor
public class ReportExecution extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(name = "report_definition_id", nullable = false)
    private UUID reportDefinitionId;

    @Column(length = 20, nullable = false)
    private String triggeredBy;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String parameters;

    @Column(name = "result_file_size")
    private Long resultFileSize;

    @Column(name = "result_file_path", length = 500)
    private String resultFilePath;

    @Column(name = "execution_duration_ms")
    private Long executionDurationMs;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    public ReportExecution(String tenantId, UUID reportDefinitionId, String triggeredBy) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.reportDefinitionId = reportDefinitionId;
        this.triggeredBy = triggeredBy;
        this.status = "QUEUED";
    }

    public void startProcessing() {
        this.status = "PROCESSING";
    }

    public void complete(Long durationMs, Long fileSize, String filePath) {
        this.status = "COMPLETED";
        this.executionDurationMs = durationMs;
        this.resultFileSize = fileSize;
        this.resultFilePath = filePath;
    }

    public void fail(String errorMessage) {
        this.status = "FAILED";
        this.errorMessage = errorMessage;
    }
}
