package com.orvion.hcm.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateEmployeeRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String position;
    private String grade;
    private String managerId;
    private String bankAccount;
    private BigDecimal basicSalary;
    private String salaryCurrency;
    private BigDecimal allowances;
}
