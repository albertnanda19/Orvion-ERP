package com.orvion.sales.domain.repository;

import com.orvion.sales.domain.model.Customer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID id);
    Optional<Customer> findByTenantIdAndCode(String tenantId, String code);
    List<Customer> findAllByTenantId(String tenantId);
    List<Customer> findByTenantIdAndCustomerType(String tenantId, String customerType);
    long countByTenantId(String tenantId);
    boolean existsByTenantIdAndCode(String tenantId, String code);
    void delete(Customer customer);
}
