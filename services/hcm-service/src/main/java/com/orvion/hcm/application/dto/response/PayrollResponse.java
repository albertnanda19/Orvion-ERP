package com.orvion.hcm.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PayrollResponse {
    private UUID id;
    private UUID employeeId;
    private int periodYear;
    private int periodMonth;
    private BigDecimal basicSalary;
    private BigDecimal allowances;
    private BigDecimal overtime;
    private BigDecimal deductions;
    private BigDecimal taxAmount;
    private BigDecimal netPay;
    private String currency;
    private String status;
    private Instant createdAt;
}
