package com.orvion.reporting.application.usecase;

import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.reporting.application.dto.response.ExecutiveDashboardResponse;
import com.orvion.reporting.application.dto.response.InventoryReportResponse;
import com.orvion.reporting.application.dto.response.SalesReportResponse;
import com.orvion.reporting.application.mapper.ReportingMapper;
import com.orvion.reporting.domain.model.*;
import com.orvion.reporting.domain.repository.*;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportGenerationTest {

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
    void generateExecutiveDashboard_shouldAggregateAllKpis() {
        String tenantId = "tenant1";
        String period = "2026-05";

        ReportFinanceFact finance = new ReportFinanceFact();
        finance.setRevenue(BigDecimal.valueOf(500000));
        finance.setExpenses(BigDecimal.valueOf(300000));

        ReportInventoryFact inventory = new ReportInventoryFact();
        inventory.setTotalProducts(100L);
        inventory.setTotalStockValue(BigDecimal.valueOf(50000));
        inventory.setLowStockCount(5L);

        ReportSalesFact sales = new ReportSalesFact();
        sales.setTotalOrders(200L);
        sales.setTotalRevenue(BigDecimal.valueOf(250000));

        ReportHcmFact hcm = new ReportHcmFact();
        hcm.setTotalEmployees(50L);
        hcm.setTotalPayroll(BigDecimal.valueOf(100000));

        var financialSummary = ExecutiveDashboardResponse.FinancialSummary.builder()
            .totalRevenue(BigDecimal.valueOf(500000))
            .totalExpenses(BigDecimal.valueOf(300000))
            .build();
        var inventorySummary = ExecutiveDashboardResponse.InventorySummary.builder()
            .totalProducts(100L)
            .inventoryValue(BigDecimal.valueOf(50000))
            .lowStockCount(5L)
            .build();
        var salesSummary = ExecutiveDashboardResponse.SalesSummary.builder()
            .totalOrders(200L)
            .totalSalesRevenue(BigDecimal.valueOf(250000))
            .build();
        var hrSummary = ExecutiveDashboardResponse.HrSummary.builder()
            .totalEmployees(50L)
            .totalPayrollCost(BigDecimal.valueOf(100000))
            .build();

        when(financeFactRepository.findByTenantIdAndPeriod(tenantId, period)).thenReturn(Optional.of(finance));
        when(inventoryFactRepository.findByTenantIdAndPeriod(tenantId, period)).thenReturn(Optional.of(inventory));
        when(salesFactRepository.findByTenantIdAndPeriod(tenantId, period)).thenReturn(Optional.of(sales));
        when(hcmFactRepository.findByTenantIdAndPeriod(tenantId, period)).thenReturn(Optional.of(hcm));
        when(mapper.toFinancialSummary(finance)).thenReturn(financialSummary);
        when(mapper.toInventorySummary(inventory)).thenReturn(inventorySummary);
        when(mapper.toSalesSummary(sales)).thenReturn(salesSummary);
        when(mapper.toHrSummary(hcm)).thenReturn(hrSummary);

        ExecutiveDashboardResponse response = useCase.generateExecutiveDashboard(tenantId, period);

        assertThat(response).isNotNull();
        assertThat(response.getFinancial().getTotalRevenue()).isEqualByComparingTo(BigDecimal.valueOf(500000));
        assertThat(response.getFinancial().getTotalExpenses()).isEqualByComparingTo(BigDecimal.valueOf(300000));
        assertThat(response.getInventory().getTotalProducts()).isEqualTo(100);
        assertThat(response.getInventory().getLowStockCount()).isEqualTo(5);
        assertThat(response.getSales().getTotalOrders()).isEqualTo(200);
        assertThat(response.getSales().getTotalSalesRevenue()).isEqualByComparingTo(BigDecimal.valueOf(250000));
        assertThat(response.getHr().getTotalEmployees()).isEqualTo(50);
        assertThat(response.getHr().getTotalPayrollCost()).isEqualByComparingTo(BigDecimal.valueOf(100000));
    }

    @Test
    void generateExecutiveDashboard_shouldHandleMissingData() {
        String tenantId = "tenant1";
        String period = "2026-05";

        when(financeFactRepository.findByTenantIdAndPeriod(tenantId, period)).thenReturn(Optional.empty());
        when(inventoryFactRepository.findByTenantIdAndPeriod(tenantId, period)).thenReturn(Optional.empty());
        when(salesFactRepository.findByTenantIdAndPeriod(tenantId, period)).thenReturn(Optional.empty());
        when(hcmFactRepository.findByTenantIdAndPeriod(tenantId, period)).thenReturn(Optional.empty());

        ExecutiveDashboardResponse response = useCase.generateExecutiveDashboard(tenantId, period);

        assertThat(response).isNotNull();
        assertThat(response.getFinancial()).isNull();
        assertThat(response.getInventory()).isNull();
        assertThat(response.getSales()).isNull();
        assertThat(response.getHr()).isNull();
    }

    @Test
    void generateSalesReport_shouldReturnSalesData() {
        String tenantId = "tenant1";
        String period = "2026-05";

        ReportSalesFact fact = new ReportSalesFact();
        fact.setPeriod(period);
        fact.setTotalOrders(100L);
        fact.setTotalRevenue(BigDecimal.valueOf(150000));
        fact.setConversionRate(BigDecimal.valueOf(3.5));
        fact.setAvgOrderValue(BigDecimal.valueOf(1500));

        SalesReportResponse salesResponse = new SalesReportResponse();
        salesResponse.setTotalOrders(100L);
        salesResponse.setTotalRevenue(BigDecimal.valueOf(150000));

        when(salesFactRepository.findByTenantIdAndPeriod(tenantId, period)).thenReturn(Optional.of(fact));
        when(mapper.toSalesReportResponse(fact)).thenReturn(salesResponse);

        SalesReportResponse response = useCase.generateSalesReport(tenantId, period);

        assertThat(response).isNotNull();
        assertThat(response.getTotalOrders()).isEqualTo(100);
        assertThat(response.getTotalRevenue()).isEqualByComparingTo(BigDecimal.valueOf(150000));
    }

    @Test
    void generateSalesReport_shouldThrowWhenNotFound() {
        when(salesFactRepository.findByTenantIdAndPeriod(anyString(), anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.generateSalesReport("t1", "2026-05"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void generateInventoryReport_shouldReturnLatestData() {
        String tenantId = "tenant1";

        ReportInventoryFact fact1 = new ReportInventoryFact();
        fact1.setId(UUID.randomUUID());
        fact1.setPeriod("2026-04");
        fact1.setTotalProducts(90L);

        ReportInventoryFact fact2 = new ReportInventoryFact();
        fact2.setId(UUID.randomUUID());
        fact2.setPeriod("2026-05");
        fact2.setTotalProducts(100L);
        fact2.setTotalStockValue(BigDecimal.valueOf(50000));
        fact2.setLowStockCount(3L);
        fact2.setTurnoverRate(BigDecimal.valueOf(2.5));

        InventoryReportResponse invResponse = new InventoryReportResponse();
        invResponse.setTotalProducts(100L);
        invResponse.setTotalStockValue(BigDecimal.valueOf(50000));
        invResponse.setLowStockCount(3L);

        when(inventoryFactRepository.findAllByTenantId(tenantId)).thenReturn(List.of(fact1, fact2));
        when(mapper.toInventoryReportResponse(fact2)).thenReturn(invResponse);

        InventoryReportResponse response = useCase.generateInventoryReport(tenantId);

        assertThat(response).isNotNull();
        assertThat(response.getTotalProducts()).isEqualTo(100);
        assertThat(response.getTotalStockValue()).isEqualByComparingTo(BigDecimal.valueOf(50000));
        assertThat(response.getLowStockCount()).isEqualTo(3);
    }

    @Test
    void generateInventoryReport_shouldThrowWhenNoData() {
        when(inventoryFactRepository.findAllByTenantId(anyString())).thenReturn(List.of());

        assertThatThrownBy(() -> useCase.generateInventoryReport("t1"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createDefinition_shouldPersistAndReturn() {
        when(definitionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ReportDefinition def = useCase.createDefinition("t1", "Monthly Sales", "Sales report",
            "SALES", null, null, "PDF");

        assertThat(def).isNotNull();
        assertThat(def.getName()).isEqualTo("Monthly Sales");
        assertThat(def.getReportType()).isEqualTo("SALES");
        assertThat(def.getOutputFormat()).isEqualTo("PDF");
        assertThat(def.isActive()).isTrue();
        assertThat(def.getTenantId()).isEqualTo("t1");
        verify(definitionRepository).save(any());
    }

    @Test
    void executeReport_shouldStartAndComplete() {
        ReportDefinition def = new ReportDefinition("t1", "Test", "SALES");
        def.setId(UUID.randomUUID());
        when(definitionRepository.findById(any())).thenReturn(Optional.of(def));
        when(executionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ReportSalesFact fact = new ReportSalesFact();
        fact.setPeriod("2026-05");
        fact.setTotalOrders(50L);
        when(salesFactRepository.findByTenantIdAndPeriod(anyString(), anyString())).thenReturn(Optional.of(fact));

        ReportExecution execution = useCase.executeReport("t1", def.getId(), "USER");

        assertThat(execution).isNotNull();
        assertThat(execution.getStatus()).isIn("COMPLETED", "PROCESSING");
        verify(executionRepository, atLeast(1)).save(any());
    }
}
