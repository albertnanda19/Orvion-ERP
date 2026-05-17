package com.orvion.finance.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJournalEntryLineRequest {
    @NotBlank
    private String accountCode;
    @NotNull
    private String side;
    @NotNull
    @Positive
    private BigDecimal amount;
    private String description;
    private String currency;
}
