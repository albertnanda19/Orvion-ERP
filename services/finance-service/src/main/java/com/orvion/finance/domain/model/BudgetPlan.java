package com.orvion.finance.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.finance.domain.model.vo.FiscalPeriod;
import com.orvion.finance.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "budget_plans", indexes = {
    @Index(name = "idx_budget_tenant_department", columnList = "tenantId, department"),
    @Index(name = "idx_budget_tenant_period", columnList = "tenantId, year, month")
})
@Getter
@Setter
@NoArgsConstructor
public class BudgetPlan extends Auditable {

    @Id
    private UUID id;

    @Column(name = "tenant_id", length = 50, nullable = false)
    private String tenantId;

    @Column(length = 100)
    private String department;

    @Column(name = "account_code", length = 20, nullable = false)
    private String accountCode;

    @Column(name = "account_name", length = 200)
    private String accountName;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "budgeted_amount", precision = 19, scale = 4, nullable = false)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "currency", length = 3))
    })
    private Money budgetedAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "actual_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "actual_currency", length = 3))
    })
    private Money actualAmount;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "month", nullable = false)
    private int month;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public BudgetPlan(String tenantId, String department, String accountCode, String accountName,
                      Money budgetedAmount, FiscalPeriod period) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.department = department;
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.budgetedAmount = budgetedAmount;
        this.actualAmount = Money.zero(budgetedAmount.getCurrencyCode());
        this.year = period.getYear();
        this.month = period.getMonth();
    }

    public Money getRemainingBudget() {
        return budgetedAmount.subtract(actualAmount);
    }

    public boolean isExceeded() {
        return actualAmount.isGreaterThan(budgetedAmount);
    }
}
