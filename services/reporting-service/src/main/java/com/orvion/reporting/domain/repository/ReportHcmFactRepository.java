package com.orvion.reporting.domain.repository;

import com.orvion.reporting.domain.model.ReportHcmFact;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportHcmFactRepository {
    ReportHcmFact save(ReportHcmFact fact);
    List<ReportHcmFact> findAllByTenantId(String tenantId);
    Optional<ReportHcmFact> findByTenantIdAndPeriod(String tenantId, String period);
}
