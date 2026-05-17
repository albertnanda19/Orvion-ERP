package com.orvion.inventory.application.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private String category;
    private String unit;
    private BigDecimal currentStock;
    private BigDecimal reservedStock;
    private BigDecimal availableStock;
    private BigDecimal reorderPoint;
    private BigDecimal reorderQuantity;
    private String preferredSupplierId;
    private String warehouseId;
    private BigDecimal standardCost;
    private String costCurrency;
    private String costingMethod;
    private boolean active;
    private Instant createdAt;
}
