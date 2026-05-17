package com.orvion.finance.infrastructure.persistence.repository;

import com.orvion.finance.domain.model.Invoice;
import com.orvion.finance.domain.model.enums.InvoiceStatus;
import com.orvion.finance.domain.repository.InvoiceRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceJpaRepository extends JpaRepository<Invoice, UUID>, InvoiceRepository {

    @Override
    Optional<Invoice> findById(UUID id);

    @Override
    @Query("SELECT i FROM Invoice i WHERE i.tenantId = :tenantId AND i.status = :status")
    List<Invoice> findByTenantIdAndStatus(@Param("tenantId") String tenantId,
                                          @Param("status") InvoiceStatus status);

    @Override
    @Query("SELECT i FROM Invoice i WHERE i.tenantId = :tenantId AND i.dueDate < CURRENT_TIMESTAMP " +
           "AND i.status NOT IN ('PAID', 'CANCELLED', 'VOID')")
    List<Invoice> findOverdueByTenantId(@Param("tenantId") String tenantId);

    @Override
    @Query("SELECT i FROM Invoice i WHERE i.counterpartyId = :counterpartyId")
    List<Invoice> findByCounterpartyId(@Param("counterpartyId") String counterpartyId);

    @Override
    @Query("SELECT i FROM Invoice i WHERE i.invoiceNumber = :invNum")
    Optional<Invoice> findByInvoiceNumber(@Param("invNum") String invoiceNumber);

    @Override
    @Query("SELECT i FROM Invoice i WHERE i.tenantId = :tenantId ORDER BY i.createdAt DESC")
    List<Invoice> findAllByTenantId(@Param("tenantId") String tenantId,
                                    Pageable pageable);

    @Override
    @Query("SELECT i FROM Invoice i WHERE i.tenantId = :tenantId AND i.type = :type ORDER BY i.createdAt DESC")
    List<Invoice> findAllByTenantIdAndType(@Param("tenantId") String tenantId,
                                           @Param("type") String type,
                                           Pageable pageable);

    @Override
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.tenantId = :tenantId AND i.status = :status")
    long countByTenantIdAndStatus(@Param("tenantId") String tenantId,
                                   @Param("status") InvoiceStatus status);
}
