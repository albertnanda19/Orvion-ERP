package com.orvion.hcm.infrastructure.persistence.jpa;

import com.orvion.hcm.domain.model.Attendance;
import com.orvion.hcm.domain.repository.AttendanceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceJpaRepository extends JpaRepository<Attendance, UUID>, AttendanceRepository {
    @Override
    Optional<Attendance> findByEmployeeIdAndDate(UUID employeeId, LocalDate date);

    @Override
    List<Attendance> findByTenantIdAndEmployeeId(String tenantId, UUID employeeId);

    @Override
    List<Attendance> findByTenantIdAndEmployeeIdAndDateBetween(String tenantId, UUID employeeId, LocalDate from, LocalDate to);
}
