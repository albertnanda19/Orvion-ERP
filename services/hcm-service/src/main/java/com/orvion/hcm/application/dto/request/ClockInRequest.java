package com.orvion.hcm.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClockInRequest {
    @NotNull private UUID employeeId;
    @NotNull private LocalDate date;
    @NotNull private LocalTime clockIn;
}
