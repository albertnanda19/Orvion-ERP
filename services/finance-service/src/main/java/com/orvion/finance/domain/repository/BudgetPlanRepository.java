package com.orvion.finance.domain.repository;

import com.orvion.finance.domain.model.BudgetPlan;

import java.util.Optional;
import java.util.UUID;

public interface BudgetPlanRepository {

    BudgetPlan save(BudgetPlan plan);

    Optional<BudgetPlan> findById(UUID id);

    Optional<BudgetPlan> findByTenantIdAndDepartmentAndAccountCodeAndPeriod(
        String tenantId, String department, String accountCode, int year, int month);
}
