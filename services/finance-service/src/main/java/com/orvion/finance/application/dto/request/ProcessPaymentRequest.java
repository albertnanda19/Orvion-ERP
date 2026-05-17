package com.orvion.finance.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentRequest {
    @NotNull
    private String invoiceId;
    @NotNull
    @Positive
    private BigDecimal amount;
    @NotNull
    private String method;
    private Instant paymentDate;
    private String reference;
    private String bankAccount;
    private String notes;
    private String currency;
}
