package com.orvion.finance.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJournalEntryRequest {
    @NotBlank
    private String reference;
    private String description;
    @NotNull
    private Instant entryDate;
    private int year;
    private int month;
    @NotEmpty(message = "At least 2 journal entry lines are required")
    @Valid
    private List<CreateJournalEntryLineRequest> lines;
}
