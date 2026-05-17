package com.orvion.inventory.application.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdjustStockRequest {
    @NotBlank private String productId;
    @NotBlank private String warehouseId;
    @NotNull private BigDecimal adjustment;
    @NotBlank private String reason;
}
