package com.orvion.inventory.application.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class IssueStockRequest {
    @NotBlank private String productId;
    @NotBlank private String warehouseId;
    @NotNull @Positive private BigDecimal quantity;
    private String reference;
    private String sourceDocument;
}
