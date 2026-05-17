package com.orvion.finance.domain.event;

import com.orvion.common.event.DomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BudgetExceededEvent extends DomainEvent {

    private final String department;
    private final String accountCode;
    private final BigDecimal budgetAmount;
    private final BigDecimal actualAmount;
    private final String period;

    public BudgetExceededEvent(String department, String accountCode,
                               BigDecimal budgetAmount, BigDecimal actualAmount,
                               String period, String tenantId) {
        super("BUDGET_EXCEEDED", tenantId, accountCode, "BUDGET_PLAN");
        this.department = department;
        this.accountCode = accountCode;
        this.budgetAmount = budgetAmount;
        this.actualAmount = actualAmount;
        this.period = period;
    }
}
