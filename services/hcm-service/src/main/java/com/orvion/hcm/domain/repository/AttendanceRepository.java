package com.orvion.hcm.domain.repository;

import com.orvion.hcm.domain.model.Attendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository {
    Attendance save(Attendance attendance);
    Optional<Attendance> findById(UUID id);
    Optional<Attendance> findByEmployeeIdAndDate(UUID employeeId, LocalDate date);
    List<Attendance> findByTenantIdAndEmployeeId(String tenantId, UUID employeeId);
    List<Attendance> findByTenantIdAndEmployeeIdAndDateBetween(String tenantId, UUID employeeId, LocalDate from, LocalDate to);
}
