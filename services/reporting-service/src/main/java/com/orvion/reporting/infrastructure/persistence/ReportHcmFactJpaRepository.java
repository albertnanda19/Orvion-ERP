package com.orvion.reporting.infrastructure.persistence;

import com.orvion.reporting.domain.model.ReportHcmFact;
import com.orvion.reporting.domain.repository.ReportHcmFactRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportHcmFactJpaRepository extends JpaRepository<ReportHcmFact, UUID>, ReportHcmFactRepository {
    @Override
    List<ReportHcmFact> findAllByTenantId(String tenantId);

    @Override
    Optional<ReportHcmFact> findByTenantIdAndPeriod(String tenantId, String period);
}
