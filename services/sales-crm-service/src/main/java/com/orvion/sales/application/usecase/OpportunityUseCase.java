package com.orvion.sales.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.sales.application.dto.request.CreateOpportunityRequest;
import com.orvion.sales.application.dto.response.OpportunityResponse;
import com.orvion.sales.application.mapper.SalesMapper;
import com.orvion.sales.domain.model.Opportunity;
import com.orvion.sales.domain.model.vo.Money;
import com.orvion.sales.domain.repository.OpportunityRepository;
import io.micrometer.core.instrument.Counter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OpportunityUseCase {
    private final OpportunityRepository opportunityRepository;
    private final SalesMapper mapper;
    private final Counter opportunitiesCreatedCounter;

    public OpportunityUseCase(OpportunityRepository opportunityRepository, SalesMapper mapper,
                              Counter opportunitiesCreatedCounter) {
        this.opportunityRepository = opportunityRepository;
        this.mapper = mapper;
        this.opportunitiesCreatedCounter = opportunitiesCreatedCounter;
    }

    @CacheEvict(value = "opportunities", allEntries = true)
    public OpportunityResponse createOpportunity(String tenantId, CreateOpportunityRequest request) {
        Opportunity opp = new Opportunity(tenantId, request.getTitle(),
            request.getLeadId() != null ? UUID.fromString(request.getLeadId()) : null,
            request.getAccountId(), request.getAssignedTo());
        if (request.getExpectedValue() != null) {
            opp.setExpectedValue(new Money(request.getExpectedValue(),
                request.getCurrency() != null ? request.getCurrency() : "IDR"));
        }
        opp.setExpectedCloseDate(request.getExpectedCloseDate());
        opp = opportunityRepository.save(opp);
        opportunitiesCreatedCounter.increment();
        return mapper.toOpportunityResponse(opp);
    }

    @CacheEvict(value = "opportunities", allEntries = true)
    public OpportunityResponse advanceStage(String tenantId, UUID oppId) {
        Opportunity opp = opportunityRepository.findById(oppId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity", "id", oppId.toString()));
        if (!opp.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Opportunity does not belong to tenant");
        opp.advanceStage();
        opp = opportunityRepository.save(opp);
        return mapper.toOpportunityResponse(opp);
    }

    @CacheEvict(value = "opportunities", allEntries = true)
    public OpportunityResponse closeWon(String tenantId, UUID oppId, BigDecimal actualValue, String currency) {
        Opportunity opp = opportunityRepository.findById(oppId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity", "id", oppId.toString()));
        if (!opp.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Opportunity does not belong to tenant");
        Money value = actualValue != null ? new Money(actualValue, currency != null ? currency : "IDR") : null;
        opp.closeWon(value);
        opp = opportunityRepository.save(opp);
        return mapper.toOpportunityResponse(opp);
    }

    @CacheEvict(value = "opportunities", allEntries = true)
    public OpportunityResponse closeLost(String tenantId, UUID oppId, String reason) {
        Opportunity opp = opportunityRepository.findById(oppId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity", "id", oppId.toString()));
        if (!opp.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Opportunity does not belong to tenant");
        opp.closeLost(reason);
        opp = opportunityRepository.save(opp);
        return mapper.toOpportunityResponse(opp);
    }

    @Cacheable(value = "opportunities", key = "#tenantId + ':' + #oppId")
    @Transactional(readOnly = true)
    public OpportunityResponse getOpportunityById(String tenantId, UUID oppId) {
        Opportunity opp = opportunityRepository.findById(oppId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity", "id", oppId.toString()));
        if (!opp.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Opportunity does not belong to tenant");
        return mapper.toOpportunityResponse(opp);
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponse> getOpportunities(String tenantId) {
        return mapper.toOpportunityResponseList(opportunityRepository.findAllByTenantId(tenantId));
    }
}
