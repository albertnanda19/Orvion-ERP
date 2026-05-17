package com.orvion.finance.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryResponse {
    private UUID id;
    private String reference;
    private String description;
    private int year;
    private int month;
    private String status;
    private Instant entryDate;
    private List<JournalEntryLineResponse> lines;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
    private String createdBy;
    private String approvedBy;
    private Instant createdAt;
}
