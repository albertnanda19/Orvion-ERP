package com.orvion.sales.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateSalesOrderRequest {
    @NotBlank private String customerId;
    private String assignedTo;
    private Instant expectedDelivery;
    @NotEmpty private List<SalesOrderLineRequest> lines;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SalesOrderLineRequest {
        @NotBlank private String productId;
        private String productName;
        private String sku;
        @NotBlank private String quantity;
        @NotBlank private String unitPrice;
        private String currency;
    }
}
