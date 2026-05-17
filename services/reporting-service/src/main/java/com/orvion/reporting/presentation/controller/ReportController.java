package com.orvion.reporting.presentation.controller;

import com.orvion.reporting.application.dto.response.*;
import com.orvion.reporting.application.mapper.ReportingMapper;
import com.orvion.reporting.application.usecase.ReportGenerationUseCase;
import com.orvion.reporting.domain.model.ReportDefinition;
import com.orvion.reporting.domain.model.ReportExecution;
import com.orvion.reporting.infrastructure.elasticsearch.ElasticsearchService;
import io.micrometer.core.instrument.Counter;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController extends BaseController {
    private final ReportGenerationUseCase reportGenerationUseCase;
    private final ElasticsearchService elasticsearchService;
    private final ReportingMapper mapper;
    private final Counter reportsGeneratedCounter;
    private final Counter reportsFailedCounter;

    public ReportController(ReportGenerationUseCase reportGenerationUseCase,
                            ElasticsearchService elasticsearchService,
                            ReportingMapper mapper,
                            Counter reportsGeneratedCounter,
                            Counter reportsFailedCounter) {
        this.reportGenerationUseCase = reportGenerationUseCase;
        this.elasticsearchService = elasticsearchService;
        this.mapper = mapper;
        this.reportsGeneratedCounter = reportsGeneratedCounter;
        this.reportsFailedCounter = reportsFailedCounter;
    }

    @GetMapping("/executive-dashboard")
    public ResponseEntity<ExecutiveDashboardResponse> getExecutiveDashboard(
            @RequestParam(defaultValue = "2026-05") String period) {
        String tenantId = extractTenantId();
        ExecutiveDashboardResponse dashboard = reportGenerationUseCase.generateExecutiveDashboard(tenantId, period);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/sales")
    public ResponseEntity<SalesReportResponse> getSalesReport(
            @RequestParam(defaultValue = "2026-05") String period) {
        String tenantId = extractTenantId();
        SalesReportResponse report = reportGenerationUseCase.generateSalesReport(tenantId, period);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/inventory")
    public ResponseEntity<InventoryReportResponse> getInventoryReport() {
        String tenantId = extractTenantId();
        InventoryReportResponse report = reportGenerationUseCase.generateInventoryReport(tenantId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/hr")
    public ResponseEntity<HrReportResponse> getHrReport(
            @RequestParam(defaultValue = "2026-05") String period) {
        String tenantId = extractTenantId();
        HrReportResponse report = reportGenerationUseCase.generateHrReport(tenantId, period);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/definitions")
    public ResponseEntity<ReportDefinitionResponse> createDefinition(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam String reportType,
            @RequestParam(required = false) String queryConfig,
            @RequestParam(required = false) String scheduleConfig,
            @RequestParam(defaultValue = "PDF") String outputFormat) {
        String tenantId = extractTenantId();
        ReportDefinition def = reportGenerationUseCase.createDefinition(
            tenantId, name, description, reportType, queryConfig, scheduleConfig, outputFormat);
        ReportDefinitionResponse resp = new ReportDefinitionResponse();
        resp.setId(def.getId());
        resp.setTenantId(def.getTenantId());
        resp.setName(def.getName());
        resp.setDescription(def.getDescription());
        resp.setReportType(def.getReportType());
        resp.setScheduleConfig(def.getScheduleConfig());
        resp.setOutputFormat(def.getOutputFormat());
        resp.setActive(def.isActive());
        resp.setCreatedAt(def.getCreatedAt());
        resp.setUpdatedAt(def.getUpdatedAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/definitions")
    public ResponseEntity<List<ReportDefinitionResponse>> listDefinitions() {
        String tenantId = extractTenantId();
        List<ReportDefinition> definitions = reportGenerationUseCase.listDefinitions(tenantId);
        List<ReportDefinitionResponse> resp = definitions.stream().map(def -> {
            ReportDefinitionResponse r = new ReportDefinitionResponse();
            r.setId(def.getId());
            r.setTenantId(def.getTenantId());
            r.setName(def.getName());
            r.setDescription(def.getDescription());
            r.setReportType(def.getReportType());
            r.setScheduleConfig(def.getScheduleConfig());
            r.setOutputFormat(def.getOutputFormat());
            r.setActive(def.isActive());
            r.setCreatedAt(def.getCreatedAt());
            r.setUpdatedAt(def.getUpdatedAt());
            return r;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/definitions/{id}/execute")
    public ResponseEntity<ReportExecutionStatusResponse> executeReport(@PathVariable UUID id) {
        String tenantId = extractTenantId();
        ReportExecution execution = reportGenerationUseCase.executeReport(tenantId, id, "USER");
        ReportExecutionStatusResponse resp = ReportExecutionStatusResponse.builder()
            .id(execution.getId())
            .reportDefinitionId(execution.getReportDefinitionId())
            .triggeredBy(execution.getTriggeredBy())
            .status(execution.getStatus())
            .resultFileSize(execution.getResultFileSize())
            .resultFilePath(execution.getResultFilePath())
            .executionDurationMs(execution.getExecutionDurationMs())
            .errorMessage(execution.getErrorMessage())
            .createdAt(execution.getCreatedAt())
            .build();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/executions/{id}/status")
    public ResponseEntity<ReportExecutionStatusResponse> getExecutionStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(ReportExecutionStatusResponse.builder()
            .id(id)
            .status("COMPLETED")
            .build());
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<Map<String, Object>>> searchAuditLogs(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            Pageable pageable) {
        String tenantId = extractTenantId();
        List<Map<String, Object>> logs = elasticsearchService.searchAuditLogs(
            tenantId, query, startDate, endDate, pageable);
        return ResponseEntity.ok(logs);
    }
}
