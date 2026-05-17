package com.orvion.hcm.domain.event;

import com.orvion.common.event.DomainEvent;
import java.time.Instant;

public class LeaveApprovedEvent extends DomainEvent {
    private final String leaveId;
    private final String employeeId;
    private final String leaveType;
    private final int durationDays;
    private final Instant startDate;
    private final Instant endDate;

    public LeaveApprovedEvent(String leaveId, String employeeId, String leaveType, int durationDays, Instant startDate, Instant endDate, String tenantId) {
        super("LEAVE_APPROVED", "LeaveRequest", leaveId, tenantId);
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.durationDays = durationDays;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getLeaveId() { return leaveId; }
    public String getEmployeeId() { return employeeId; }
    public String getLeaveType() { return leaveType; }
    public int getDurationDays() { return durationDays; }
    public Instant getStartDate() { return startDate; }
    public Instant getEndDate() { return endDate; }
}
