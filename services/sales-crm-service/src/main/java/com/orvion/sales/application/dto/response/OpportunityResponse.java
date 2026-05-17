package com.orvion.sales.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OpportunityResponse {
    private UUID id;
    private String title;
    private UUID leadId;
    private String accountId;
    private String assignedTo;
    private String stage;
    private int probability;
    private BigDecimal expectedValue;
    private String currency;
    private Instant expectedCloseDate;
    private Instant createdAt;
}
