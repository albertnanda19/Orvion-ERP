package com.orvion.reporting.infrastructure.persistence;

import com.orvion.reporting.domain.model.ReportFinanceFact;
import com.orvion.reporting.domain.repository.ReportFinanceFactRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportFinanceFactJpaRepository extends JpaRepository<ReportFinanceFact, UUID>, ReportFinanceFactRepository {
    @Override
    List<ReportFinanceFact> findAllByTenantId(String tenantId);

    @Override
    Optional<ReportFinanceFact> findByTenantIdAndPeriod(String tenantId, String period);
}
