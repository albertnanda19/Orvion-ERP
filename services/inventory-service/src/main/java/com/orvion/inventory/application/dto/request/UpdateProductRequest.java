package com.orvion.inventory.application.dto.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateProductRequest {
    private String name;
    private String description;
    private String category;
    private BigDecimal reorderPoint;
    private BigDecimal reorderQuantity;
    private String preferredSupplierId;
    private String warehouseId;
}
