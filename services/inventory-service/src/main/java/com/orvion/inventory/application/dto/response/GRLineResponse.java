package com.orvion.inventory.application.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GRLineResponse {
    private UUID id;
    private UUID purchaseOrderLineId;
    private UUID productId;
    private String productName;
    private BigDecimal quantity;
    private BigDecimal acceptedQuantity;
    private BigDecimal rejectedQuantity;
    private BigDecimal unitCost;
    private String currency;
}
