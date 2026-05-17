package com.orvion.inventory.application.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PurchaseOrderResponse {
    private UUID id;
    private String poNumber;
    private String supplierId;
    private String supplierName;
    private String status;
    private List<POLineResponse> lines;
    private BigDecimal totalAmount;
    private String currency;
    private Instant orderDate;
    private Instant expectedDelivery;
    private String approvedBy;
    private Instant approvedAt;
    private String notes;
    private Instant createdAt;
}


