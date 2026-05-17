package com.orvion.finance.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrialBalanceEntry {
    private String accountCode;
    private String accountName;
    private String accountType;
    private BigDecimal debitBalance;
    private BigDecimal creditBalance;
}
