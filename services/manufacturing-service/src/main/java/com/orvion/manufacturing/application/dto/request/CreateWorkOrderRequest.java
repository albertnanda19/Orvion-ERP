package com.orvion.manufacturing.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateWorkOrderRequest {
    @NotBlank private String productId;
    @Positive @NotNull private BigDecimal plannedQuantity;
    private String bomId;
    @NotNull private Instant plannedStart;
    @NotNull private Instant plannedEnd;
    private String warehouseId;
}
