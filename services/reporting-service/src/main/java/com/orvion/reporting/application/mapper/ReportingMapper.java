package com.orvion.reporting.application.mapper;

import com.orvion.reporting.application.dto.response.*;
import com.orvion.reporting.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportingMapper {

    @Mapping(target = "totalRevenue", source = "revenue")
    @Mapping(target = "totalExpenses", source = "expenses")
    ExecutiveDashboardResponse.FinancialSummary toFinancialSummary(ReportFinanceFact fact);

    @Mapping(target = "inventoryValue", source = "totalStockValue")
    ExecutiveDashboardResponse.InventorySummary toInventorySummary(ReportInventoryFact fact);

    @Mapping(target = "totalSalesRevenue", source = "totalRevenue")
    ExecutiveDashboardResponse.SalesSummary toSalesSummary(ReportSalesFact fact);

    @Mapping(target = "totalPayrollCost", source = "totalPayroll")
    ExecutiveDashboardResponse.HrSummary toHrSummary(ReportHcmFact fact);

    SalesReportResponse toSalesReportResponse(ReportSalesFact fact);
    List<SalesReportResponse> toSalesReportResponseList(List<ReportSalesFact> facts);

    InventoryReportResponse toInventoryReportResponse(ReportInventoryFact fact);

    @Mapping(target = "totalPayrollCost", source = "totalPayroll")
    HrReportResponse toHrReportResponse(ReportHcmFact fact);
}
