package com.orvion.reporting.application.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InventoryReportResponse {
    private String period;
    private Long totalProducts;
    private BigDecimal totalStockValue;
    private Long lowStockCount;
    private BigDecimal turnoverRate;
}
