package com.orvion.inventory.application.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SupplierResponse {
    private UUID id;
    private String code;
    private String name;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String paymentTerms;
    private BigDecimal performanceScore;
    private boolean active;
    private Instant createdAt;
}
