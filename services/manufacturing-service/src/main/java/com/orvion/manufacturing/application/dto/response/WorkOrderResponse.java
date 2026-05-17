package com.orvion.manufacturing.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WorkOrderResponse {
    private UUID id;
    private String orderNumber;
    private String productId;
    private BigDecimal plannedQuantity;
    private BigDecimal actualQuantity;
    private String bomId;
    private String status;
    private Instant plannedStart;
    private Instant plannedEnd;
    private Instant actualStart;
    private Instant actualEnd;
    private String warehouseId;
    private Instant createdAt;
}
