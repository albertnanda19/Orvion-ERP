package com.orvion.inventory.application.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateProductRequest {
    @NotBlank private String sku;
    @NotBlank private String name;
    private String description;
    private String category;
    @NotBlank private String unit;
    private BigDecimal reorderPoint;
    private BigDecimal reorderQuantity;
    private String preferredSupplierId;
    private String warehouseId;
    private BigDecimal standardCost;
    private String costCurrency;
    private String costingMethod;
}
