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
public class POLineRequest {
    @NotBlank private String productId;
    private String productName;
    private String sku;
    @NotNull @Positive private BigDecimal quantity;
    @NotNull private BigDecimal unitPrice;
    private String currency;
}
