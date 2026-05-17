package com.orvion.reporting.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ReportExecutionStatusResponse {
    private UUID id;
    private UUID reportDefinitionId;
    private String triggeredBy;
    private String status;
    private Long resultFileSize;
    private String resultFilePath;
    private Long executionDurationMs;
    private String errorMessage;
    private Instant createdAt;
}
