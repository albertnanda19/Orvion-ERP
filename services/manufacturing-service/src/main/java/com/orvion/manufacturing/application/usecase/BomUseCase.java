package com.orvion.manufacturing.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.manufacturing.application.dto.request.CreateBomRequest;
import com.orvion.manufacturing.application.dto.response.BomResponse;
import com.orvion.manufacturing.application.mapper.ManufacturingMapper;
import com.orvion.manufacturing.domain.model.BillOfMaterials;
import com.orvion.manufacturing.domain.repository.BomRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BomUseCase {
    private final BomRepository bomRepository;
    private final ManufacturingMapper mapper;

    public BomUseCase(BomRepository bomRepository, ManufacturingMapper mapper) {
        this.bomRepository = bomRepository;
        this.mapper = mapper;
    }

    @CacheEvict(value = "bom", allEntries = true)
    public BomResponse createBom(String tenantId, CreateBomRequest request) {
        BillOfMaterials bom = new BillOfMaterials(tenantId, request.getProductId(), request.getVersion());
        for (CreateBomRequest.BomComponentRequest c : request.getComponents()) {
            bom.addComponent(c.getComponentProductId(), c.getQuantity(), c.getUnit(), c.getWastePercentage());
        }
        bom = bomRepository.save(bom);
        return mapper.toBomResponse(bom);
    }

    @Cacheable(value = "bom", key = "#tenantId + ':' + #bomId")
    @Transactional(readOnly = true)
    public BomResponse getBomById(String tenantId, UUID bomId) {
        BillOfMaterials bom = bomRepository.findById(bomId)
            .orElseThrow(() -> new ResourceNotFoundException("BillOfMaterials", "id", bomId.toString()));
        if (!bom.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "BOM does not belong to tenant");
        return mapper.toBomResponse(bom);
    }

    @Transactional(readOnly = true)
    public List<BomResponse> getActiveBoms(String tenantId) {
        return mapper.toBomResponseList(bomRepository.findAllByTenantIdAndActiveTrue(tenantId));
    }

    @CacheEvict(value = "bom", allEntries = true)
    public void deactivateBom(String tenantId, UUID bomId) {
        BillOfMaterials bom = bomRepository.findById(bomId)
            .orElseThrow(() -> new ResourceNotFoundException("BillOfMaterials", "id", bomId.toString()));
        if (!bom.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "BOM does not belong to tenant");
        bom.deactivate();
        bomRepository.save(bom);
    }
}
