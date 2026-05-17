package com.orvion.finance.domain.repository;

import com.orvion.finance.domain.model.Invoice;
import com.orvion.finance.domain.model.enums.InvoiceStatus;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {

    Invoice save(Invoice invoice);

    Optional<Invoice> findById(UUID id);

    List<Invoice> findByTenantIdAndStatus(String tenantId, InvoiceStatus status);

    List<Invoice> findOverdueByTenantId(String tenantId);

    List<Invoice> findByCounterpartyId(String counterpartyId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findAllByTenantId(String tenantId, Pageable pageable);

    List<Invoice> findAllByTenantIdAndType(String tenantId, String type, Pageable pageable);

    long countByTenantIdAndStatus(String tenantId, InvoiceStatus status);
}
