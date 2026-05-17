package com.orvion.finance.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryLineResponse {
    private UUID id;
    private UUID accountId;
    private String accountCode;
    private String accountName;
    private String side;
    private BigDecimal amount;
    private String currency;
    private String description;
    private int lineNumber;
}
