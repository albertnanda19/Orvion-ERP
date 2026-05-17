package com.orvion.reporting.infrastructure.persistence;

import com.orvion.reporting.domain.model.ReportInventoryFact;
import com.orvion.reporting.domain.repository.ReportInventoryFactRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportInventoryFactJpaRepository extends JpaRepository<ReportInventoryFact, UUID>, ReportInventoryFactRepository {
    @Override
    List<ReportInventoryFact> findAllByTenantId(String tenantId);

    @Override
    Optional<ReportInventoryFact> findByTenantIdAndPeriod(String tenantId, String period);
}
