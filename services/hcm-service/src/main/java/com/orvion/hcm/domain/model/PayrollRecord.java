package com.orvion.hcm.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.hcm.domain.model.enums.FiscalPeriod;
import com.orvion.hcm.domain.model.enums.PayrollStatus;
import com.orvion.hcm.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payroll_records", indexes = {
    @Index(name = "idx_pr_tenant_period", columnList = "tenantId, periodYear, periodMonth"),
    @Index(name = "idx_pr_employee", columnList = "employeeId")
})
@Getter @Setter @NoArgsConstructor
public class PayrollRecord extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private UUID employeeId;

    @Column(nullable = false)
    private int periodYear;

    @Column(nullable = false)
    private int periodMonth;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "basic_salary", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "currency", length = 3))
    })
    private Money basicSalary;

    @Column(precision = 19, scale = 4)
    private BigDecimal allowances;

    @Column(precision = 19, scale = 4)
    private BigDecimal overtime;

    @Column(precision = 19, scale = 4)
    private BigDecimal deductions;

    @Column(precision = 19, scale = 4)
    private BigDecimal taxAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "net_pay", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "net_pay_currency", length = 3))
    })
    private Money netPay;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PayrollStatus status = PayrollStatus.DRAFT;

    public PayrollRecord(String tenantId, UUID employeeId, FiscalPeriod period, Money basicSalary) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.employeeId = employeeId;
        this.periodYear = period.getYear();
        this.periodMonth = period.getMonth();
        this.basicSalary = basicSalary;
        this.status = PayrollStatus.DRAFT;
    }

    public void calculate() {
        if (status != PayrollStatus.DRAFT) {
            throw new BusinessException("INVALID_STATUS", "Payroll must be in DRAFT status to calculate");
        }
        Money allowancesMoney = allowances != null
            ? new Money(allowances, basicSalary.getCurrencyCode())
            : Money.zero(basicSalary.getCurrencyCode());
        Money overtimeMoney = overtime != null
            ? new Money(overtime, basicSalary.getCurrencyCode())
            : Money.zero(basicSalary.getCurrencyCode());
        Money deductionsMoney = deductions != null
            ? new Money(deductions, basicSalary.getCurrencyCode())
            : Money.zero(basicSalary.getCurrencyCode());
        Money taxMoney = taxAmount != null
            ? new Money(taxAmount, basicSalary.getCurrencyCode())
            : Money.zero(basicSalary.getCurrencyCode());

        Money gross = basicSalary.add(allowancesMoney).add(overtimeMoney);
        Money totalDeductions = deductionsMoney.add(taxMoney);
        this.netPay = gross.subtract(totalDeductions);
        this.status = PayrollStatus.PROCESSED;
    }

    public void approve() {
        if (status != PayrollStatus.PROCESSED) {
            throw new BusinessException("INVALID_STATUS", "Payroll must be PROCESSED before approving");
        }
        this.status = PayrollStatus.PAID;
    }

    public void markPaid() {
        if (status != PayrollStatus.PROCESSED) {
            throw new BusinessException("INVALID_STATUS", "Payroll must be PROCESSED before marking as paid");
        }
        this.status = PayrollStatus.PAID;
    }
}
