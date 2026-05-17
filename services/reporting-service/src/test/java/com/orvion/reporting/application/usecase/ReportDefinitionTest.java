package com.orvion.reporting.application.usecase;

import com.orvion.reporting.application.mapper.ReportingMapper;
import com.orvion.reporting.domain.model.*;
import com.orvion.reporting.domain.repository.*;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportDefinitionTest {

    @Mock private ReportDefinitionRepository definitionRepository;
    @Mock private ReportExecutionRepository executionRepository;
    @Mock private ReportFinanceFactRepository financeFactRepository;
    @Mock private ReportInventoryFactRepository inventoryFactRepository;
    @Mock private ReportSalesFactRepository salesFactRepository;
    @Mock private ReportHcmFactRepository hcmFactRepository;
    @Mock private Counter reportsGeneratedCounter;
    @Mock private Counter reportsFailedCounter;
    @Mock private ReportingMapper mapper;
    private ReportGenerationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReportGenerationUseCase(
            definitionRepository, executionRepository,
            financeFactRepository, inventoryFactRepository,
            salesFactRepository, hcmFactRepository,
            mapper, reportsGeneratedCounter, reportsFailedCounter
        );
    }

    @Test
    void createDefinition_shouldSetCorrectDefaults() {
        when(definitionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ReportDefinition def = useCase.createDefinition("t1", "Quarterly Finance",
            "Q2 finance summary", "FINANCIAL", "{}", "0 0 1 4 * ?", "EXCEL");

        assertThat(def.getTenantId()).isEqualTo("t1");
        assertThat(def.getName()).isEqualTo("Quarterly Finance");
        assertThat(def.getReportType()).isEqualTo("FINANCIAL");
        assertThat(def.getQueryConfig()).isEqualTo("{}");
        assertThat(def.getScheduleConfig()).isEqualTo("0 0 1 4 * ?");
        assertThat(def.getOutputFormat()).isEqualTo("EXCEL");
        assertThat(def.isActive()).isTrue();
        verify(definitionRepository).save(def);
    }

    @Test
    void createDefinition_shouldDefaultOutputFormatToPdf() {
        when(definitionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ReportDefinition def = useCase.createDefinition("t1", "Test", null, "CUSTOM",
            null, null, null);

        assertThat(def.getOutputFormat()).isEqualTo("PDF");
    }

    @Test
    void listDefinitions_shouldReturnAllForTenant() {
        String tenantId = "t1";
        ReportDefinition def1 = new ReportDefinition(tenantId, "Report 1", "SALES");
        ReportDefinition def2 = new ReportDefinition(tenantId, "Report 2", "INVENTORY");
        when(definitionRepository.findAllByTenantId(tenantId)).thenReturn(List.of(def1, def2));

        List<ReportDefinition> results = useCase.listDefinitions(tenantId);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getName()).isEqualTo("Report 1");
        assertThat(results.get(1).getName()).isEqualTo("Report 2");
    }

    @Test
    void checkScheduledReports_shouldExecuteActiveScheduled() {
        ReportDefinition def = new ReportDefinition("t1", "Auto Report", "SALES");
        def.setId(UUID.randomUUID());
        def.setScheduleConfig("0 0 * * * *");

        when(definitionRepository.findActiveScheduledReports()).thenReturn(List.of(def));
        when(definitionRepository.findById(def.getId())).thenReturn(Optional.of(def));
        when(executionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ReportSalesFact fact = new ReportSalesFact();
        fact.setPeriod("2026-05");
        fact.setTotalOrders(10L);
        when(salesFactRepository.findByTenantIdAndPeriod(anyString(), anyString())).thenReturn(Optional.of(fact));

        useCase.checkScheduledReports();

        verify(executionRepository, atLeastOnce()).save(any());
        verify(definitionRepository).findActiveScheduledReports();
    }

    @Test
    void listDefinitions_shouldReturnEmptyWhenNoneExist() {
        when(definitionRepository.findAllByTenantId("t1")).thenReturn(List.of());

        List<ReportDefinition> results = useCase.listDefinitions("t1");

        assertThat(results).isEmpty();
    }
}
