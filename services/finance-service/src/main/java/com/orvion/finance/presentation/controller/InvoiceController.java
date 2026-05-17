package com.orvion.finance.presentation.controller;

import com.orvion.finance.application.dto.request.CreateInvoiceRequest;
import com.orvion.finance.application.dto.request.ProcessPaymentRequest;
import com.orvion.finance.application.dto.response.InvoiceResponse;
import com.orvion.finance.application.dto.response.PaymentResponse;
import com.orvion.finance.application.usecase.InvoiceUseCase;
import com.orvion.finance.domain.model.enums.InvoiceStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/finance/invoices")
@Tag(name = "Invoices", description = "Accounts Payable and Receivable management")
public class InvoiceController {

    private final InvoiceUseCase invoiceUseCase;

    public InvoiceController(InvoiceUseCase invoiceUseCase) {
        this.invoiceUseCase = invoiceUseCase;
    }

    @PostMapping
    @Operation(summary = "Create a new invoice")
    @PreAuthorize("hasAnyRole('FINANCE_MANAGER', 'FINANCE_STAFF', 'SUPER_ADMIN')")
    public ResponseEntity<InvoiceResponse> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request,
            HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        String userId = extractUserId(httpRequest);
        InvoiceResponse response = invoiceUseCase.createInvoice(tenantId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve an invoice")
    @PreAuthorize("hasAnyRole('FINANCE_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<InvoiceResponse> approveInvoice(
            @PathVariable UUID id, HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        String userId = extractUserId(httpRequest);
        InvoiceResponse response = invoiceUseCase.approveInvoice(tenantId, userId, id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/payments")
    @Operation(summary = "Process a payment for an invoice")
    @PreAuthorize("hasAnyRole('FINANCE_MANAGER', 'FINANCE_STAFF', 'SUPER_ADMIN')")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable UUID id,
            @Valid @RequestBody ProcessPaymentRequest request,
            HttpServletRequest httpRequest) {
        request.setInvoiceId(id.toString());
        String tenantId = extractTenantId(httpRequest);
        String userId = extractUserId(httpRequest);
        PaymentResponse response = invoiceUseCase.processPayment(tenantId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<InvoiceResponse> getInvoice(
            @PathVariable UUID id, HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        InvoiceResponse response = invoiceUseCase.getInvoice(tenantId, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List invoices")
    public ResponseEntity<Page<InvoiceResponse>> listInvoices(
            @RequestParam(required = false) InvoiceStatus status,
            Pageable pageable, HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        Page<InvoiceResponse> invoices = invoiceUseCase.getInvoices(tenantId, status, pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue invoices")
    public ResponseEntity<List<InvoiceResponse>> getOverdueInvoices(HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        List<InvoiceResponse> invoices = invoiceUseCase.getOverdueInvoices(tenantId);
        return ResponseEntity.ok(invoices);
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
