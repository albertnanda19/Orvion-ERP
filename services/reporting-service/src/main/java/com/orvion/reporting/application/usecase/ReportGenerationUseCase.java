package com.orvion.reporting.application.usecase;

import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.reporting.application.dto.response.*;
import com.orvion.reporting.application.mapper.ReportingMapper;
import com.orvion.reporting.domain.model.*;
import com.orvion.reporting.domain.repository.*;
import com.orvion.reporting.infrastructure.config.ReportingMetricsConfig;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ReportGenerationUseCase {
    private static final Logger log = LoggerFactory.getLogger(ReportGenerationUseCase.class);
    private final ReportDefinitionRepository definitionRepository;
    private final ReportExecutionRepository executionRepository;
    private final ReportFinanceFactRepository financeFactRepository;
    private final ReportInventoryFactRepository inventoryFactRepository;
    private final ReportSalesFactRepository salesFactRepository;
    private final ReportHcmFactRepository hcmFactRepository;
    private final ReportingMapper mapper;
    private final Counter reportsGeneratedCounter;
    private final Counter reportsFailedCounter;

    public ReportGenerationUseCase(ReportDefinitionRepository definitionRepository,
                                    ReportExecutionRepository executionRepository,
                                    ReportFinanceFactRepository financeFactRepository,
                                    ReportInventoryFactRepository inventoryFactRepository,
                                    ReportSalesFactRepository salesFactRepository,
                                    ReportHcmFactRepository hcmFactRepository,
                                    ReportingMapper mapper,
                                    Counter reportsGeneratedCounter,
                                    Counter reportsFailedCounter) {
        this.definitionRepository = definitionRepository;
        this.executionRepository = executionRepository;
        this.financeFactRepository = financeFactRepository;
        this.inventoryFactRepository = inventoryFactRepository;
        this.salesFactRepository = salesFactRepository;
        this.hcmFactRepository = hcmFactRepository;
        this.mapper = mapper;
        this.reportsGeneratedCounter = reportsGeneratedCounter;
        this.reportsFailedCounter = reportsFailedCounter;
    }

    @Cacheable(value = "dashboard", key = "#tenantId + ':' + #period")
    public ExecutiveDashboardResponse generateExecutiveDashboard(String tenantId, String period) {
        ReportFinanceFact finance = financeFactRepository.findByTenantIdAndPeriod(tenantId, period)
            .orElse(null);
        ReportInventoryFact inventory = inventoryFactRepository.findByTenantIdAndPeriod(tenantId, period)
            .orElse(null);
        ReportSalesFact sales = salesFactRepository.findByTenantIdAndPeriod(tenantId, period)
            .orElse(null);
        ReportHcmFact hcm = hcmFactRepository.findByTenantIdAndPeriod(tenantId, period)
            .orElse(null);

        ExecutiveDashboardResponse.FinancialSummary financial = null;
        if (finance != null) financial = mapper.toFinancialSummary(finance);

        ExecutiveDashboardResponse.InventorySummary inv = null;
        if (inventory != null) inv = mapper.toInventorySummary(inventory);

        ExecutiveDashboardResponse.SalesSummary salesSummary = null;
        if (sales != null) salesSummary = mapper.toSalesSummary(sales);

        ExecutiveDashboardResponse.HrSummary hr = null;
        if (hcm != null) hr = mapper.toHrSummary(hcm);

        return ExecutiveDashboardResponse.builder()
            .financial(financial)
            .inventory(inv)
            .sales(salesSummary)
            .hr(hr)
            .build();
    }

    @Transactional
    public SalesReportResponse generateSalesReport(String tenantId, String period) {
        ReportSalesFact fact = salesFactRepository.findByTenantIdAndPeriod(tenantId, period)
            .orElseThrow(() -> new ResourceNotFoundException("Sales data not found for period: " + period));
        reportsGeneratedCounter.increment();
        return mapper.toSalesReportResponse(fact);
    }

    @Transactional
    public InventoryReportResponse generateInventoryReport(String tenantId) {
        List<ReportInventoryFact> facts = inventoryFactRepository.findAllByTenantId(tenantId);
        if (facts.isEmpty()) throw new ResourceNotFoundException("No inventory data found");
        ReportInventoryFact latest = facts.get(facts.size() - 1);
        reportsGeneratedCounter.increment();
        return mapper.toInventoryReportResponse(latest);
    }

    @Transactional
    public HrReportResponse generateHrReport(String tenantId, String period) {
        ReportHcmFact fact = hcmFactRepository.findByTenantIdAndPeriod(tenantId, period)
            .orElseThrow(() -> new ResourceNotFoundException("HR data not found for period: " + period));
        reportsGeneratedCounter.increment();
        return mapper.toHrReportResponse(fact);
    }

    @Transactional
    public ReportDefinition createDefinition(String tenantId, String name, String description,
                                              String reportType, String queryConfig,
                                              String scheduleConfig, String outputFormat) {
        ReportDefinition def = new ReportDefinition(tenantId, name, reportType);
        def.setDescription(description);
        def.setQueryConfig(queryConfig);
        def.setScheduleConfig(scheduleConfig);
        def.setOutputFormat(outputFormat != null ? outputFormat : "PDF");
        return definitionRepository.save(def);
    }

    public List<ReportDefinition> listDefinitions(String tenantId) {
        return definitionRepository.findAllByTenantId(tenantId);
    }

    @Transactional
    public ReportExecution executeReport(String tenantId, UUID definitionId, String triggeredBy) {
        ReportDefinition def = definitionRepository.findById(definitionId)
            .orElseThrow(() -> new ResourceNotFoundException("Report definition not found"));
        ReportExecution execution = new ReportExecution(tenantId, def.getId(), triggeredBy);
        execution = executionRepository.save(execution);
        try {
            execution.startProcessing();
            long startTime = System.currentTimeMillis();
            String reportType = def.getReportType();
            switch (reportType) {
                case "SALES" -> generateSalesReport(tenantId, "2026-05");
                case "INVENTORY" -> generateInventoryReport(tenantId);
                case "HR" -> generateHrReport(tenantId, "2026-05");
                default -> generateExecutiveDashboard(tenantId, "2026-05");
            }
            long duration = System.currentTimeMillis() - startTime;
            execution.complete(duration, null, null);
            reportsGeneratedCounter.increment();
        } catch (Exception e) {
            log.error("Report execution failed for definition {}: {}", definitionId, e.getMessage());
            execution.fail(e.getMessage());
            reportsFailedCounter.increment();
        }
        return executionRepository.save(execution);
    }

    @Scheduled(cron = "0 0 * * * *")
    @CacheEvict(value = {"dashboard", "reports"}, allEntries = true)
    public void checkScheduledReports() {
        log.info("Checking scheduled reports...");
        List<ReportDefinition> scheduled = definitionRepository.findActiveScheduledReports();
        for (ReportDefinition def : scheduled) {
            try {
                executeReport(def.getTenantId(), def.getId(), "SCHEDULE");
                log.info("Scheduled report {} executed successfully", def.getName());
            } catch (Exception e) {
                log.error("Failed to execute scheduled report {}: {}", def.getName(), e.getMessage());
            }
        }
    }
}
