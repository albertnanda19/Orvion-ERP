package com.orvion.hcm.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InitiatePayrollRequest {
    @NotNull private UUID employeeId;
    @NotNull private Integer periodYear;
    @NotNull private Integer periodMonth;
    private BigDecimal basicSalary;
    private BigDecimal allowances;
    private BigDecimal overtime;
    private BigDecimal deductions;
    private BigDecimal taxAmount;
    private String currency;
}
