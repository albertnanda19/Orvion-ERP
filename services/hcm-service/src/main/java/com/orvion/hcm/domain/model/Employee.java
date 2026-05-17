package com.orvion.hcm.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.hcm.domain.model.enums.EmploymentStatus;
import com.orvion.hcm.domain.model.enums.EmploymentType;
import com.orvion.hcm.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "employees", indexes = {
    @Index(name = "idx_emp_tenant_dept", columnList = "tenantId, department"),
    @Index(name = "idx_emp_tenant_status", columnList = "tenantId, employmentStatus"),
    @Index(name = "idx_emp_employee_id", columnList = "employeeId", unique = true)
})
@Getter @Setter @NoArgsConstructor
public class Employee extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 20, unique = true, nullable = false)
    private String employeeId;

    @Column(length = 100, nullable = false)
    private String firstName;

    @Column(length = 100, nullable = false)
    private String lastName;

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(length = 50)
    private String nationalId;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String position;

    @Column(length = 20)
    private String grade;

    @Column(length = 50)
    private String managerId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EmploymentStatus employmentStatus;

    @Column(name = "join_date")
    private Instant joinDate;

    @Column(name = "termination_date")
    private Instant terminationDate;

    @Column(length = 50)
    private String bankAccount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "basic_salary", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "salary_currency", length = 3))
    })
    private Money basicSalary;

    @Column(precision = 19, scale = 4)
    private BigDecimal allowances;

    @Column(nullable = false)
    private boolean active = true;

    public Employee(String tenantId, String firstName, String lastName, String email,
                    EmploymentType employmentType, String department, String position) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.employeeId = generateEmployeeId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.employmentType = employmentType;
        this.employmentStatus = EmploymentStatus.PROBATION;
        this.department = department;
        this.position = position;
        this.active = true;
        this.joinDate = Instant.now();
    }

    private String generateEmployeeId() {
        return "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void terminate(String reason, Instant date) {
        if (employmentStatus == EmploymentStatus.TERMINATED) {
            throw new BusinessException("ALREADY_TERMINATED", "Employee is already terminated");
        }
        this.employmentStatus = EmploymentStatus.TERMINATED;
        this.terminationDate = date;
        this.active = false;
    }

    public void promote(String newPosition, Money newSalary) {
        if (employmentStatus == EmploymentStatus.TERMINATED) {
            throw new BusinessException("EMPLOYEE_TERMINATED", "Cannot promote a terminated employee");
        }
        this.position = newPosition;
        if (newSalary != null) {
            this.basicSalary = newSalary;
        }
    }

    public void suspend(String reason) {
        if (employmentStatus == EmploymentStatus.TERMINATED) {
            throw new BusinessException("EMPLOYEE_TERMINATED", "Cannot suspend a terminated employee");
        }
        this.employmentStatus = EmploymentStatus.SUSPENDED;
    }

    public void completeProbation() {
        if (employmentStatus != EmploymentStatus.PROBATION) {
            throw new BusinessException("NOT_IN_PROBATION", "Employee is not in probation period");
        }
        this.employmentStatus = EmploymentStatus.ACTIVE;
    }
}
