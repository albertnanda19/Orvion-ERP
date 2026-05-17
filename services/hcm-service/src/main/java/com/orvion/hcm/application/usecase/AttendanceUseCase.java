package com.orvion.hcm.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.hcm.application.dto.request.ClockInRequest;
import com.orvion.hcm.application.dto.request.ClockOutRequest;
import com.orvion.hcm.application.dto.response.AttendanceResponse;
import com.orvion.hcm.application.mapper.HcmMapper;
import com.orvion.hcm.domain.model.Attendance;
import com.orvion.hcm.domain.model.Employee;
import com.orvion.hcm.domain.repository.AttendanceRepository;
import com.orvion.hcm.domain.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AttendanceUseCase {
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final HcmMapper mapper;

    public AttendanceUseCase(AttendanceRepository attendanceRepository,
                             EmployeeRepository employeeRepository,
                             HcmMapper mapper) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    public AttendanceResponse clockIn(String tenantId, ClockInRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId().toString()));
        if (!employee.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Employee does not belong to tenant");

        attendanceRepository.findByEmployeeIdAndDate(request.getEmployeeId(), request.getDate())
            .ifPresent(a -> { throw new BusinessException("ALREADY_CLOCKED_IN", "Already clocked in for this date"); });

        Attendance attendance = new Attendance(tenantId, request.getEmployeeId(), request.getDate(), request.getClockIn());
        attendance = attendanceRepository.save(attendance);
        return mapper.toAttendanceResponse(attendance);
    }

    public AttendanceResponse clockOut(String tenantId, ClockOutRequest request) {
        Attendance attendance = attendanceRepository.findById(request.getAttendanceId())
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", "id", request.getAttendanceId().toString()));
        attendance.clockOut(request.getClockOut());
        attendance = attendanceRepository.save(attendance);
        return mapper.toAttendanceResponse(attendance);
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getEmployeeAttendance(String tenantId, UUID employeeId) {
        return mapper.toAttendanceResponseList(
            attendanceRepository.findByTenantIdAndEmployeeId(tenantId, employeeId));
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByDateRange(String tenantId, UUID employeeId, LocalDate from, LocalDate to) {
        return mapper.toAttendanceResponseList(
            attendanceRepository.findByTenantIdAndEmployeeIdAndDateBetween(tenantId, employeeId, from, to));
    }
}
