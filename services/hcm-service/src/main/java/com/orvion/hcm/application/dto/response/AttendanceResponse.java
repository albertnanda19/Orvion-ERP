package com.orvion.hcm.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceResponse {
    private UUID id;
    private UUID employeeId;
    private LocalDate date;
    private LocalTime clockIn;
    private LocalTime clockOut;
    private Duration workingHours;
    private boolean isLate;
    private boolean isOvertime;
}
