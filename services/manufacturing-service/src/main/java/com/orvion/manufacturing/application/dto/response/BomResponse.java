package com.orvion.manufacturing.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BomResponse {
    private UUID id;
    private String productId;
    private int version;
    private List<BomComponentResponse> components;
    private Instant effectiveDate;
    private boolean active;
    private Instant createdAt;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BomComponentResponse {
        private UUID id;
        private String componentProductId;
        private BigDecimal quantity;
        private String unit;
        private BigDecimal wastePercentage;
    }
}
