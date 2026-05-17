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
public class GoodsReceiptResponse {
    private UUID id;
    private String receiptNumber;
    private UUID purchaseOrderId;
    private UUID warehouseId;
    private String receivedBy;
    private Instant receivedAt;
    private List<GRLineResponse> lines;
    private String notes;
    private String status;
    private Instant createdAt;
}


