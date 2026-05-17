package com.orvion.finance.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceDashboardResponse {
    private BigDecimal totalOutstandingInvoices;
    private BigDecimal totalOverdueAmount;
    private long overdueCount;
    private long pendingApprovalCount;
    private BigDecimal cashBalance;
    private BigDecimal accountsReceivable;
    private BigDecimal accountsPayable;
    private BigDecimal monthlyRevenue;
    private BigDecimal monthlyExpenses;
    private String currency;
}
