package com.orvion.reporting.application.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HrReportResponse {
    private String period;
    private Long totalEmployees;
    private BigDecimal totalPayrollCost;
    private String deptCounts;
}
