package com.orvion.finance.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID id;
    private UUID invoiceId;
    private BigDecimal amount;
    private String currency;
    private String method;
    private Instant paymentDate;
    private String reference;
    private String bankAccount;
    private boolean reconciled;
    private Instant createdAt;
}
