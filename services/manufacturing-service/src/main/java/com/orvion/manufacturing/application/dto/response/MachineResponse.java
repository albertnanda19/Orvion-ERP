package com.orvion.manufacturing.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MachineResponse {
    private UUID id;
    private String machineId;
    private String name;
    private String type;
    private String status;
    private BigDecimal oeeTarget;
    private Instant lastMaintenanceDate;
    private Instant nextMaintenanceDate;
    private Instant createdAt;
}
