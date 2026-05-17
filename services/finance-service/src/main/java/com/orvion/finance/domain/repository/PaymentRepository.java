package com.orvion.finance.domain.repository;

import com.orvion.finance.domain.model.Payment;

import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(UUID id);

    List<Payment> findByInvoiceId(UUID invoiceId);

    List<Payment> findByTenantIdAndDateRange(String tenantId, Instant start, Instant end);

    List<Payment> findAllByTenantId(String tenantId, Pageable pageable);
}
