package com.orvion.hcm.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeResponse {
    private UUID id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String nationalId;
    private String department;
    private String position;
    private String grade;
    private String managerId;
    private String employmentType;
    private String employmentStatus;
    private Instant joinDate;
    private Instant terminationDate;
    private String bankAccount;
    private BigDecimal basicSalary;
    private String salaryCurrency;
    private BigDecimal allowances;
    private boolean active;
    private Instant createdAt;
}
