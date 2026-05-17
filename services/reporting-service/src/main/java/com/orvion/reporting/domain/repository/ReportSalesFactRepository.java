package com.orvion.reporting.domain.repository;

import com.orvion.reporting.domain.model.ReportSalesFact;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportSalesFactRepository {
    ReportSalesFact save(ReportSalesFact fact);
    List<ReportSalesFact> findAllByTenantId(String tenantId);
    Optional<ReportSalesFact> findByTenantIdAndPeriod(String tenantId, String period);
}
