package com.orvion.hcm.domain.event;

import com.orvion.common.event.DomainEvent;

public class EmployeeOnboardedEvent extends DomainEvent {
    private final String employeeId;
    private final String fullName;
    private final String department;
    private final String position;

    public EmployeeOnboardedEvent(String employeeId, String fullName, String department, String position, String tenantId) {
        super("EMPLOYEE_ONBOARDED", "Employee", employeeId, tenantId);
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.department = department;
        this.position = position;
    }

    public String getEmployeeId() { return employeeId; }
    public String getFullName() { return fullName; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
}
