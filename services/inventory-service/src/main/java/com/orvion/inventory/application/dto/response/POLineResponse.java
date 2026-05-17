package com.orvion.inventory.application.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class POLineResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private String sku;
    private BigDecimal quantity;
    private BigDecimal receivedQuantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String currency;
}
