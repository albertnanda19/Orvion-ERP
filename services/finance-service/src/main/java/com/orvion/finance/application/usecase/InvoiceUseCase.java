package com.orvion.finance.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.finance.application.dto.request.CreateInvoiceLineItemRequest;
import com.orvion.finance.application.dto.request.CreateInvoiceRequest;
import com.orvion.finance.application.dto.request.ProcessPaymentRequest;
import com.orvion.finance.application.dto.response.InvoiceResponse;
import com.orvion.finance.application.dto.response.PaymentResponse;
import com.orvion.finance.application.mapper.InvoiceMapper;
import com.orvion.finance.application.mapper.PaymentMapper;
import com.orvion.finance.domain.event.InvoiceApprovedEvent;
import com.orvion.finance.domain.event.InvoiceCreatedEvent;
import com.orvion.finance.domain.event.PaymentProcessedEvent;
import com.orvion.finance.domain.model.Invoice;
import com.orvion.finance.domain.model.InvoiceLineItem;
import com.orvion.finance.domain.model.Payment;
import com.orvion.finance.domain.model.enums.InvoiceStatus;
import com.orvion.finance.domain.model.enums.InvoiceType;
import com.orvion.finance.domain.model.enums.PaymentMethod;
import com.orvion.finance.domain.model.vo.Money;
import com.orvion.finance.domain.repository.InvoiceRepository;
import com.orvion.finance.domain.repository.PaymentRepository;
import com.orvion.finance.infrastructure.messaging.FinanceEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvoiceUseCase {

    private static final Logger log = LoggerFactory.getLogger(InvoiceUseCase.class);

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceMapper invoiceMapper;
    private final PaymentMapper paymentMapper;
    private final FinanceEventPublisher eventPublisher;

    public InvoiceUseCase(InvoiceRepository invoiceRepository,
                           PaymentRepository paymentRepository,
                           InvoiceMapper invoiceMapper,
                           PaymentMapper paymentMapper,
                           FinanceEventPublisher eventPublisher) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.invoiceMapper = invoiceMapper;
        this.paymentMapper = paymentMapper;
        this.eventPublisher = eventPublisher;
    }

    public InvoiceResponse createInvoice(String tenantId, String userId, CreateInvoiceRequest request) {
        InvoiceType type = InvoiceType.valueOf(request.getType());
        Invoice invoice = new Invoice(tenantId, type, request.getCounterpartyId(),
            request.getCounterpartyName(), request.getIssueDate(),
            request.getDueDate(), request.getCurrency(), request.getNotes());

        for (CreateInvoiceLineItemRequest itemReq : request.getLineItems()) {
            InvoiceLineItem line = new InvoiceLineItem(itemReq.getDescription(),
                itemReq.getQuantity(), itemReq.getUnitPrice(),
                itemReq.getTaxRate(), request.getCurrency());
            invoice.addLineItem(line);
        }

        invoice = invoiceRepository.save(invoice);

        InvoiceCreatedEvent event = new InvoiceCreatedEvent(
            invoice.getId().toString(), invoice.getInvoiceNumber(),
            type, invoice.getTotalAmount().getAmount(),
            request.getCurrency(), request.getCounterpartyId(), tenantId);
        eventPublisher.publishInvoiceCreated(event);

        log.info("Invoice created: id={}, number={}, tenant={}",
            invoice.getId(), invoice.getInvoiceNumber(), tenantId);
        return invoiceMapper.toResponse(invoice);
    }

    public InvoiceResponse approveInvoice(String tenantId, String approverId, UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId.toString()));

        if (!invoice.getTenantId().equals(tenantId)) {
            throw new BusinessException("TENANT_MISMATCH", "Invoice does not belong to tenant");
        }

        invoice.submitForApproval();
        invoice.approve(approverId);
        invoice = invoiceRepository.save(invoice);

        InvoiceApprovedEvent event = new InvoiceApprovedEvent(
            invoice.getId().toString(), invoice.getInvoiceNumber(),
            approverId, invoice.getTotalAmount().getAmount(), tenantId);
        eventPublisher.publishEvent(event.getEventType(), event.getAggregateType(),
            event.getAggregateId(), tenantId, serializeEvent(event), "orvion.finance.invoice.approved");

        log.info("Invoice approved: id={}, number={}, approver={}",
            invoice.getId(), invoice.getInvoiceNumber(), approverId);
        return invoiceMapper.toResponse(invoice);
    }

    public PaymentResponse processPayment(String tenantId, String userId, ProcessPaymentRequest request) {
        UUID invoiceId = UUID.fromString(request.getInvoiceId());
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId.toString()));

        if (!invoice.getTenantId().equals(tenantId)) {
            throw new BusinessException("TENANT_MISMATCH", "Invoice does not belong to tenant");
        }

        String currency = request.getCurrency() != null ? request.getCurrency() : "IDR";
        Money amount = new Money(request.getAmount(), currency);
        PaymentMethod method = PaymentMethod.valueOf(request.getMethod());

        Payment payment = new Payment(tenantId, invoiceId, amount, method,
            request.getPaymentDate() != null ? request.getPaymentDate() : Instant.now(),
            request.getReference(), request.getBankAccount(), request.getNotes());
        payment = paymentRepository.save(payment);

        invoice.applyPayment(amount);
        invoiceRepository.save(invoice);

        PaymentProcessedEvent event = new PaymentProcessedEvent(
            payment.getId().toString(), invoiceId.toString(),
            amount.getAmount(), method, tenantId);
        eventPublisher.publishPaymentProcessed(event);

        log.info("Payment processed: id={}, invoice={}, amount={}, tenant={}",
            payment.getId(), invoiceId, amount, tenantId);
        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoice(String tenantId, UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId.toString()));
        if (!invoice.getTenantId().equals(tenantId)) {
            throw new BusinessException("TENANT_MISMATCH", "Invoice does not belong to tenant");
        }
        return invoiceMapper.toResponse(invoice);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getInvoices(String tenantId, InvoiceStatus status, Pageable pageable) {
        List<Invoice> invoices;
        long total;
        if (status != null) {
            invoices = invoiceRepository.findByTenantIdAndStatus(tenantId, status);
            total = invoiceRepository.countByTenantIdAndStatus(tenantId, status);
        } else {
            invoices = invoiceRepository.findAllByTenantId(tenantId, pageable);
            total = invoices.size();
        }
        return new PageImpl<>(invoiceMapper.toResponseList(invoices), pageable, total);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getOverdueInvoices(String tenantId) {
        List<Invoice> invoices = invoiceRepository.findOverdueByTenantId(tenantId);
        return invoiceMapper.toResponseList(invoices);
    }

    private String serializeEvent(Object event) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                .writeValueAsString(event);
        } catch (Exception e) {
            log.error("Failed to serialize event: {}", e.getMessage());
            return "{}";
        }
    }
}
