package com.orvion.reporting.application.dto.response;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class ReportDefinitionResponse {
    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private String reportType;
    private String scheduleConfig;
    private String outputFormat;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
