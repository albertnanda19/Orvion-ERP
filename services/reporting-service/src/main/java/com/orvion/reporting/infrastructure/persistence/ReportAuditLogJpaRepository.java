package com.orvion.reporting.infrastructure.persistence;

import com.orvion.reporting.domain.model.ReportAuditLog;
import com.orvion.reporting.domain.repository.ReportAuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ReportAuditLogJpaRepository extends JpaRepository<ReportAuditLog, UUID>, ReportAuditLogRepository {
    @Override
    Page<ReportAuditLog> findAllByTenantId(String tenantId, Pageable pageable);
}
