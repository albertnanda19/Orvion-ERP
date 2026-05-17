package com.orvion.hcm.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClockOutRequest {
    @NotNull private UUID attendanceId;
    @NotNull private LocalTime clockOut;
}
