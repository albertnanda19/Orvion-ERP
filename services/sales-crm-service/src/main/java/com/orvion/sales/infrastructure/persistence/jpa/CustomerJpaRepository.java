package com.orvion.sales.infrastructure.persistence.jpa;

import com.orvion.sales.domain.model.Customer;
import com.orvion.sales.domain.repository.CustomerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerJpaRepository extends JpaRepository<Customer, UUID>, CustomerRepository {
    @Override
    Optional<Customer> findByTenantIdAndCode(String tenantId, String code);

    @Override
    List<Customer> findAllByTenantId(String tenantId);

    @Override
    List<Customer> findByTenantIdAndCustomerType(String tenantId, String customerType);

    @Override
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);

    @Override
    boolean existsByTenantIdAndCode(String tenantId, String code);
}
