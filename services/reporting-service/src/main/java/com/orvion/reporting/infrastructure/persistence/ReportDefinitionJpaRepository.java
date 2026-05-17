package com.orvion.reporting.infrastructure.persistence;

import com.orvion.reporting.domain.model.ReportDefinition;
import com.orvion.reporting.domain.repository.ReportDefinitionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReportDefinitionJpaRepository extends JpaRepository<ReportDefinition, UUID>, ReportDefinitionRepository {
    @Override
    List<ReportDefinition> findAllByTenantId(String tenantId);

    @Override
    @Query("SELECT r FROM ReportDefinition r WHERE r.active = true AND r.scheduleConfig IS NOT NULL")
    List<ReportDefinition> findActiveScheduledReports();
}
