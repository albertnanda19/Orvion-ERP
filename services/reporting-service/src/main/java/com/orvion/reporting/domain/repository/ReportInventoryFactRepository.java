package com.orvion.reporting.domain.repository;

import com.orvion.reporting.domain.model.ReportInventoryFact;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportInventoryFactRepository {
    ReportInventoryFact save(ReportInventoryFact fact);
    List<ReportInventoryFact> findAllByTenantId(String tenantId);
    Optional<ReportInventoryFact> findByTenantIdAndPeriod(String tenantId, String period);
}
