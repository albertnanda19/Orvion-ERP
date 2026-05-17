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
public class GRLineRequest {
    @NotBlank private String purchaseOrderLineId;
    @NotBlank private String productId;
    private String productName;
    @NotNull @Positive private BigDecimal quantity;
    @NotNull @Positive private BigDecimal acceptedQuantity;
    private BigDecimal rejectedQuantity;
    @NotNull private BigDecimal unitCost;
    private String currency;
}
