package com.orvion.reporting.domain.repository;

import com.orvion.reporting.domain.model.ReportAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface ReportAuditLogRepository {
    ReportAuditLog save(ReportAuditLog log);
    Page<ReportAuditLog> findAllByTenantId(String tenantId, Pageable pageable);
}
