package com.orvion.inventory.application.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StockMovementResponse {
    private UUID id;
    private UUID productId;
    private UUID warehouseId;
    private String movementType;
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private String currency;
    private String reference;
    private String sourceDocument;
    private Instant movementDate;
    private String performedBy;
}
