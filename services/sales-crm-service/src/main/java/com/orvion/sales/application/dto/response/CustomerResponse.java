package com.orvion.sales.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerResponse {
    private UUID id;
    private String code;
    private String name;
    private String email;
    private String phone;
    private String address;
    private BigDecimal creditLimit;
    private String creditCurrency;
    private BigDecimal outstanding;
    private String outstandingCurrency;
    private String paymentTerms;
    private String customerType;
    private Instant createdAt;
}
