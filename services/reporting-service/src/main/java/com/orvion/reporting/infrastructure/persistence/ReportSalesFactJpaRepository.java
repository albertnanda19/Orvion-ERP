package com.orvion.reporting.infrastructure.persistence;

import com.orvion.reporting.domain.model.ReportSalesFact;
import com.orvion.reporting.domain.repository.ReportSalesFactRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportSalesFactJpaRepository extends JpaRepository<ReportSalesFact, UUID>, ReportSalesFactRepository {
    @Override
    List<ReportSalesFact> findAllByTenantId(String tenantId);

    @Override
    Optional<ReportSalesFact> findByTenantIdAndPeriod(String tenantId, String period);
}
