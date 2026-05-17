package com.orvion.reporting.application.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalesReportResponse {
    private String period;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal conversionRate;
    private BigDecimal avgOrderValue;
}
