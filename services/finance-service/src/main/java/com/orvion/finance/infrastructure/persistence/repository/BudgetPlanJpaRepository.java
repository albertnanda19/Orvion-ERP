package com.orvion.finance.infrastructure.persistence.repository;

import com.orvion.finance.domain.model.BudgetPlan;
import com.orvion.finance.domain.repository.BudgetPlanRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetPlanJpaRepository extends JpaRepository<BudgetPlan, UUID>, BudgetPlanRepository {

    @Override
    Optional<BudgetPlan> findById(UUID id);

    @Override
    @Query("SELECT b FROM BudgetPlan b WHERE b.tenantId = :tenantId AND b.department = :dept " +
           "AND b.accountCode = :acctCode AND b.year = :year AND b.month = :month")
    Optional<BudgetPlan> findByTenantIdAndDepartmentAndAccountCodeAndPeriod(
        @Param("tenantId") String tenantId,
        @Param("dept") String department,
        @Param("acctCode") String accountCode,
        @Param("year") int year,
        @Param("month") int month);
}
