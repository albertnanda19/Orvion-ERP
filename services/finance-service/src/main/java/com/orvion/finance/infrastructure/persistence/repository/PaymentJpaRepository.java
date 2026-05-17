package com.orvion.finance.infrastructure.persistence.repository;

import com.orvion.finance.domain.model.Payment;
import com.orvion.finance.domain.repository.PaymentRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentJpaRepository extends JpaRepository<Payment, UUID>, PaymentRepository {

    @Override
    Optional<Payment> findById(UUID id);

    @Override
    @Query("SELECT p FROM Payment p WHERE p.invoiceId = :invoiceId")
    List<Payment> findByInvoiceId(@Param("invoiceId") UUID invoiceId);

    @Override
    @Query("SELECT p FROM Payment p WHERE p.tenantId = :tenantId AND p.paymentDate BETWEEN :start AND :end")
    List<Payment> findByTenantIdAndDateRange(@Param("tenantId") String tenantId,
                                              @Param("start") Instant start,
                                              @Param("end") Instant end);

    @Override
    @Query("SELECT p FROM Payment p WHERE p.tenantId = :tenantId ORDER BY p.paymentDate DESC")
    List<Payment> findAllByTenantId(@Param("tenantId") String tenantId,
                                    Pageable pageable);
}
