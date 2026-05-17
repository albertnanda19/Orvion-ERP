package com.orvion.hcm.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SubmitLeaveRequest {
    @NotNull private UUID employeeId;
    @NotBlank private String leaveType;
    @NotNull private Instant startDate;
    @NotNull private Instant endDate;
}
