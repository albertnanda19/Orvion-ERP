package com.orvion.finance.presentation.controller;

import com.orvion.finance.application.dto.request.CreateJournalEntryRequest;
import com.orvion.finance.application.dto.response.JournalEntryResponse;
import com.orvion.finance.application.usecase.JournalEntryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/finance/journal-entries")
@Tag(name = "Journal Entries", description = "Double-entry bookkeeping management")
public class JournalEntryController {

    private final JournalEntryUseCase journalEntryUseCase;

    public JournalEntryController(JournalEntryUseCase journalEntryUseCase) {
        this.journalEntryUseCase = journalEntryUseCase;
    }

    @PostMapping
    @Operation(summary = "Create a journal entry")
    @PreAuthorize("hasAnyRole('FINANCE_MANAGER', 'FINANCE_STAFF', 'SUPER_ADMIN')")
    public ResponseEntity<JournalEntryResponse> createJournalEntry(
            @Valid @RequestBody CreateJournalEntryRequest request,
            HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        String userId = extractUserId(httpRequest);
        JournalEntryResponse response = journalEntryUseCase.createJournalEntry(tenantId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/post")
    @Operation(summary = "Post a journal entry")
    @PreAuthorize("hasAnyRole('FINANCE_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<JournalEntryResponse> postJournalEntry(
            @PathVariable UUID id, HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        JournalEntryResponse response = journalEntryUseCase.postJournalEntry(tenantId, id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reverse")
    @Operation(summary = "Reverse a journal entry")
    @PreAuthorize("hasAnyRole('FINANCE_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<JournalEntryResponse> reverseJournalEntry(
            @PathVariable UUID id, @RequestParam String reason,
            HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        JournalEntryResponse response = journalEntryUseCase.reverseJournalEntry(tenantId, id, reason);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List journal entries by period")
    public ResponseEntity<List<JournalEntryResponse>> listJournalEntries(
            @RequestParam int year, @RequestParam int month,
            HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        List<JournalEntryResponse> entries = journalEntryUseCase.getJournalEntriesByPeriod(tenantId, year, month);
        return ResponseEntity.ok(entries);
    }

    private String extractTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader("X-Tenant-Id");
        if (tenantId == null || tenantId.isBlank()) {
            throw new com.orvion.common.exception.UnauthorizedException("Missing X-Tenant-Id header");
        }
        return tenantId;
    }

    private String extractUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        return userId != null ? userId : "SYSTEM";
    }
}
