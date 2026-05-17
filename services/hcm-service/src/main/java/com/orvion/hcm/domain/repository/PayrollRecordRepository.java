package com.orvion.hcm.domain.repository;

import com.orvion.hcm.domain.model.PayrollRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PayrollRecordRepository {
    PayrollRecord save(PayrollRecord record);
    Optional<PayrollRecord> findById(UUID id);
    List<PayrollRecord> findByTenantIdAndPeriodYearAndPeriodMonth(String tenantId, int year, int month);
    List<PayrollRecord> findByTenantIdAndEmployeeId(String tenantId, UUID employeeId);
    List<PayrollRecord> findByTenantIdAndEmployeeIdAndPeriodYearAndPeriodMonth(String tenantId, UUID employeeId, int year, int month);
}
