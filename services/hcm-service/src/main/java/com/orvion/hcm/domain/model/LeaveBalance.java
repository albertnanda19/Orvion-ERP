package com.orvion.hcm.domain.model;

import com.orvion.hcm.domain.model.enums.LeaveType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "leave_balances", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "year", "leave_type"})
})
@Getter @NoArgsConstructor
public class LeaveBalance {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID employeeId;

    @Column(nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private LeaveType leaveType;

    @Column(precision = 5, scale = 1, nullable = false)
    private BigDecimal totalDays;

    @Column(precision = 5, scale = 1, nullable = false)
    private BigDecimal usedDays;

    public LeaveBalance(UUID employeeId, int year, LeaveType leaveType, BigDecimal totalDays) {
        this.id = UUID.randomUUID();
        this.employeeId = employeeId;
        this.year = year;
        this.leaveType = leaveType;
        this.totalDays = totalDays;
        this.usedDays = BigDecimal.ZERO;
    }

    public BigDecimal getRemainingDays() {
        return totalDays.subtract(usedDays);
    }
}
