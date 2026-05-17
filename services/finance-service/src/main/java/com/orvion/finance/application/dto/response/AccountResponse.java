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
public class AccountResponse {
    private UUID id;
    private String code;
    private String name;
    private String type;
    private String description;
    private boolean active;
    private UUID parentAccountId;
    private int level;
    private BigDecimal balance;
    private String currency;
    private String balanceType;
    private int childCount;
}
