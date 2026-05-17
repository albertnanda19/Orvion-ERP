package com.orvion.reporting.domain.repository;

import com.orvion.reporting.domain.model.ReportFinanceFact;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportFinanceFactRepository {
    ReportFinanceFact save(ReportFinanceFact fact);
    List<ReportFinanceFact> findAllByTenantId(String tenantId);
    Optional<ReportFinanceFact> findByTenantIdAndPeriod(String tenantId, String period);
}
