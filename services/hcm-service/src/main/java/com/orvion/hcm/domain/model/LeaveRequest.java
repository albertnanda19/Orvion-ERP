package com.orvion.hcm.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.hcm.domain.model.enums.LeaveStatus;
import com.orvion.hcm.domain.model.enums.LeaveType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "leave_requests", indexes = {
    @Index(name = "idx_lr_tenant_employee", columnList = "tenantId, employeeId"),
    @Index(name = "idx_lr_tenant_status", columnList = "tenantId, status")
})
@Getter @Setter @NoArgsConstructor
public class LeaveRequest extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private UUID employeeId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant endDate;

    @Column(nullable = false)
    private int durationDays;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING;

    @Column(length = 100)
    private String approvedBy;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    public LeaveRequest(String tenantId, UUID employeeId, LeaveType leaveType, Instant startDate, Instant endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BusinessException("INVALID_DATE_RANGE", "End date must be after start date");
        }
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        this.status = LeaveStatus.PENDING;
    }

    public void approve(String managerId) {
        if (status != LeaveStatus.PENDING) {
            throw new BusinessException("INVALID_STATUS", "Leave request must be PENDING to approve");
        }
        this.status = LeaveStatus.APPROVED;
        this.approvedBy = managerId;
    }

    public void reject(String reason) {
        if (status != LeaveStatus.PENDING) {
            throw new BusinessException("INVALID_STATUS", "Leave request must be PENDING to reject");
        }
        this.status = LeaveStatus.REJECTED;
        this.rejectionReason = reason;
    }

    public void cancel() {
        if (status == LeaveStatus.APPROVED || status == LeaveStatus.PENDING) {
            this.status = LeaveStatus.CANCELLED;
        } else {
            throw new BusinessException("INVALID_STATUS", "Cannot cancel a " + status + " leave request");
        }
    }
}
