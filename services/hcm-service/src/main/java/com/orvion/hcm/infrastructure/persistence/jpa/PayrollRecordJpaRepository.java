package com.orvion.hcm.infrastructure.persistence.jpa;

import com.orvion.hcm.domain.model.PayrollRecord;
import com.orvion.hcm.domain.repository.PayrollRecordRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollRecordJpaRepository extends JpaRepository<PayrollRecord, UUID>, PayrollRecordRepository {
    @Override
    List<PayrollRecord> findByTenantIdAndPeriodYearAndPeriodMonth(String tenantId, int year, int month);

    @Override
    List<PayrollRecord> findByTenantIdAndEmployeeId(String tenantId, UUID employeeId);

    @Override
    List<PayrollRecord> findByTenantIdAndEmployeeIdAndPeriodYearAndPeriodMonth(String tenantId, UUID employeeId, int year, int month);
}
