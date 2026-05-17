package com.orvion.inventory.application.dto.request;
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
public class CreateGoodsReceiptRequest {
    @NotBlank private String purchaseOrderId;
    @NotBlank private String warehouseId;
    private String receivedBy;
    private Instant receivedAt;
    private String notes;
    @NotEmpty private List<GRLineRequest> lines;
}


