package com.orvion.reporting.domain.repository;

import com.orvion.reporting.domain.model.ReportDefinition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportDefinitionRepository {
    ReportDefinition save(ReportDefinition definition);
    Optional<ReportDefinition> findById(UUID id);
    List<ReportDefinition> findAllByTenantId(String tenantId);
    List<ReportDefinition> findActiveScheduledReports();
}
