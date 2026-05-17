package com.orvion.sales.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.sales.application.dto.request.CreateCustomerRequest;
import com.orvion.sales.application.dto.response.CustomerResponse;
import com.orvion.sales.application.mapper.SalesMapper;
import com.orvion.sales.domain.model.Customer;
import com.orvion.sales.domain.model.enums.CustomerType;
import com.orvion.sales.domain.model.vo.Money;
import com.orvion.sales.domain.repository.CustomerRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CustomerUseCase {
    private final CustomerRepository customerRepository;
    private final SalesMapper mapper;

    public CustomerUseCase(CustomerRepository customerRepository, SalesMapper mapper) {
        this.customerRepository = customerRepository;
        this.mapper = mapper;
    }

    @CacheEvict(value = "customers", allEntries = true)
    public CustomerResponse createCustomer(String tenantId, CreateCustomerRequest request) {
        CustomerType type;
        try {
            type = CustomerType.valueOf(request.getCustomerType());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("INVALID_CUSTOMER_TYPE", "Invalid customer type: " + request.getCustomerType());
        }
        Customer customer = new Customer(tenantId, request.getName(), request.getEmail(),
            request.getPhone(), request.getAddress(), type);
        if (request.getCreditLimit() != null) {
            customer.setCreditLimit(new Money(request.getCreditLimit(),
                request.getCreditCurrency() != null ? request.getCreditCurrency() : "IDR"));
        }
        if (request.getPaymentTerms() != null) {
            customer.setPaymentTerms(request.getPaymentTerms());
        }
        customer = customerRepository.save(customer);
        return mapper.toCustomerResponse(customer);
    }

    @Cacheable(value = "customers", key = "#tenantId + ':' + #customerId")
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(String tenantId, UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId.toString()));
        if (!customer.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Customer does not belong to tenant");
        return mapper.toCustomerResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getCustomers(String tenantId) {
        return mapper.toCustomerResponseList(customerRepository.findAllByTenantId(tenantId));
    }
}
