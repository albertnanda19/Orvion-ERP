package com.orvion.inventory.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.inventory.application.dto.request.CreateSupplierRequest;
import com.orvion.inventory.application.dto.response.SupplierResponse;
import com.orvion.inventory.application.mapper.InventoryMapper;
import com.orvion.inventory.domain.model.Supplier;
import com.orvion.inventory.domain.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SupplierUseCase {
    private final SupplierRepository supplierRepository;
    private final InventoryMapper mapper;

    public SupplierUseCase(SupplierRepository supplierRepository, InventoryMapper mapper) {
        this.supplierRepository = supplierRepository;
        this.mapper = mapper;
    }

    public SupplierResponse createSupplier(String tenantId, CreateSupplierRequest request) {
        if (supplierRepository.existsByTenantIdAndCode(tenantId, request.getCode()))
            throw new BusinessException("DUPLICATE_SUPPLIER_CODE", "Supplier with code " + request.getCode() + " already exists");
        Supplier supplier = new Supplier(tenantId, request.getCode(), request.getName());
        supplier.setContactEmail(request.getContactEmail());
        supplier.setContactPhone(request.getContactPhone());
        supplier.setAddress(request.getAddress());
        supplier.setPaymentTerms(request.getPaymentTerms());
        supplier.setPerformanceScore(java.math.BigDecimal.valueOf(100));
        supplier = supplierRepository.save(supplier);
        return mapper.toSupplierResponse(supplier);
    }

    public SupplierResponse updateSupplier(String tenantId, UUID supplierId, CreateSupplierRequest request) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierId.toString()));
        if (!supplier.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Supplier does not belong to tenant");
        if (request.getName() != null) supplier.setName(request.getName());
        if (request.getContactEmail() != null) supplier.setContactEmail(request.getContactEmail());
        if (request.getContactPhone() != null) supplier.setContactPhone(request.getContactPhone());
        if (request.getAddress() != null) supplier.setAddress(request.getAddress());
        if (request.getPaymentTerms() != null) supplier.setPaymentTerms(request.getPaymentTerms());
        supplier = supplierRepository.save(supplier);
        return mapper.toSupplierResponse(supplier);
    }

    @Transactional(readOnly = true)
    public List<SupplierResponse> getSuppliers(String tenantId) {
        return mapper.toSupplierResponseList(supplierRepository.findAllByTenantIdAndActiveTrue(tenantId));
    }
}
