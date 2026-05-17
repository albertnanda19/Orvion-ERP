package com.orvion.hcm.presentation.controller;

import com.orvion.hcm.application.dto.request.ClockInRequest;
import com.orvion.hcm.application.dto.request.ClockOutRequest;
import com.orvion.hcm.application.dto.response.AttendanceResponse;
import com.orvion.hcm.application.usecase.AttendanceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hcm/attendance")
@Tag(name = "Attendance", description = "Attendance management endpoints")
public class AttendanceController extends BaseController {
    private final AttendanceUseCase attendanceUseCase;

    public AttendanceController(AttendanceUseCase attendanceUseCase) { this.attendanceUseCase = attendanceUseCase; }

    @PostMapping("/clock-in")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Clock in")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN')")
    public AttendanceResponse clockIn(@Valid @RequestBody ClockInRequest request) {
        return attendanceUseCase.clockIn(extractTenantId(), request);
    }

    @PutMapping("/clock-out")
    @Operation(summary = "Clock out")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN')")
    public AttendanceResponse clockOut(@Valid @RequestBody ClockOutRequest request) {
        return attendanceUseCase.clockOut(extractTenantId(), request);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get attendance by employee")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<AttendanceResponse> getEmployeeAttendance(@PathVariable UUID employeeId) {
        return attendanceUseCase.getEmployeeAttendance(extractTenantId(), employeeId);
    }

    @GetMapping("/employee/{employeeId}/range")
    @Operation(summary = "Get attendance by date range")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<AttendanceResponse> getAttendanceByDateRange(@PathVariable UUID employeeId,
                                                              @RequestParam LocalDate from,
                                                              @RequestParam LocalDate to) {
        return attendanceUseCase.getAttendanceByDateRange(extractTenantId(), employeeId, from, to);
    }
}
