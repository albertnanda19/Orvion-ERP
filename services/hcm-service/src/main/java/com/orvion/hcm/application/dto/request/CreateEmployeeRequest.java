package com.orvion.hcm.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateEmployeeRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @Email private String email;
    private String phone;
    private String nationalId;
    @NotBlank private String department;
    @NotBlank private String position;
    private String grade;
    private String managerId;
    @NotBlank private String employmentType;
    private String bankAccount;
    private BigDecimal basicSalary;
    private String salaryCurrency;
    private BigDecimal allowances;
}
