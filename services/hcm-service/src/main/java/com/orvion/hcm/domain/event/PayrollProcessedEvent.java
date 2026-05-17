package com.orvion.hcm.domain.event;

import com.orvion.common.event.DomainEvent;
import java.math.BigDecimal;

public class PayrollProcessedEvent extends DomainEvent {
    private final String payrollId;
    private final String employeeId;
    private final BigDecimal netPay;
    private final int periodYear;
    private final int periodMonth;

    public PayrollProcessedEvent(String payrollId, String employeeId, BigDecimal netPay, int periodYear, int periodMonth, String tenantId) {
        super("PAYROLL_PROCESSED", "PayrollRecord", payrollId, tenantId);
        this.payrollId = payrollId;
        this.employeeId = employeeId;
        this.netPay = netPay;
        this.periodYear = periodYear;
        this.periodMonth = periodMonth;
    }

    public String getPayrollId() { return payrollId; }
    public String getEmployeeId() { return employeeId; }
    public BigDecimal getNetPay() { return netPay; }
    public int getPeriodYear() { return periodYear; }
    public int getPeriodMonth() { return periodMonth; }
}
