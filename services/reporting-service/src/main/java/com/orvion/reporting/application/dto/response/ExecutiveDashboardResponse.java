package com.orvion.reporting.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ExecutiveDashboardResponse {
    private FinancialSummary financial;
    private InventorySummary inventory;
    private SalesSummary sales;
    private HrSummary hr;

    @Data
    @Builder
    public static class FinancialSummary {
        private BigDecimal totalRevenue;
        private BigDecimal totalExpenses;
        private BigDecimal netProfit;
        private BigDecimal grossMargin;
    }

    @Data
    @Builder
    public static class InventorySummary {
        private Long totalProducts;
        private BigDecimal inventoryValue;
        private Long lowStockCount;
        private BigDecimal turnoverRate;
    }

    @Data
    @Builder
    public static class SalesSummary {
        private Long totalOrders;
        private BigDecimal totalSalesRevenue;
        private BigDecimal conversionRate;
        private BigDecimal avgOrderValue;
    }

    @Data
    @Builder
    public static class HrSummary {
        private Long totalEmployees;
        private BigDecimal totalPayrollCost;
    }
}
