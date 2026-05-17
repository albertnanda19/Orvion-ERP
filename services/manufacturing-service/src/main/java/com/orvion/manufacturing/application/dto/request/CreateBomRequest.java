package com.orvion.manufacturing.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateBomRequest {
    @NotBlank private String productId;
    @Positive private int version;
    @NotEmpty private List<BomComponentRequest> components;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BomComponentRequest {
        @NotBlank private String componentProductId;
        @Positive private BigDecimal quantity;
        @NotBlank private String unit;
        private BigDecimal wastePercentage;
    }
}
