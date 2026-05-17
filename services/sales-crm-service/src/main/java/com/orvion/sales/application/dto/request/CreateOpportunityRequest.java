package com.orvion.sales.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateOpportunityRequest {
    @NotBlank private String title;
    private String leadId;
    private String accountId;
    private String assignedTo;
    private BigDecimal expectedValue;
    private String currency;
    private Instant expectedCloseDate;
}
