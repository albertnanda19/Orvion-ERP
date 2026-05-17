package com.orvion.hcm.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LeaveRequestResponse {
    private UUID id;
    private UUID employeeId;
    private String leaveType;
    private Instant startDate;
    private Instant endDate;
    private int durationDays;
    private String status;
    private String approvedBy;
    private String rejectionReason;
    private Instant createdAt;
}
