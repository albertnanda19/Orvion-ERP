package com.orvion.sales.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SalesOrderResponse {
    private UUID id;
    private String orderNumber;
    private String customerId;
    private String assignedTo;
    private String status;
    private BigDecimal totalAmount;
    private String currency;
    private Instant orderDate;
    private Instant expectedDelivery;
    private Instant createdAt;
    private List<SalesOrderLineResponse> lines;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SalesOrderLineResponse {
        private UUID id;
        private String productId;
        private String productName;
        private String sku;
        private BigDecimal quantity;
        private BigDecimal reservedQuantity;
        private BigDecimal unitPrice;
        private String unitCurrency;
        private BigDecimal lineTotal;
        private String lineCurrency;
    }
}
