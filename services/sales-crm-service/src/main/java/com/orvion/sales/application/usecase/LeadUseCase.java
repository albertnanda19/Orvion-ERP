package com.orvion.sales.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.sales.application.dto.request.CreateLeadRequest;
import com.orvion.sales.application.dto.response.LeadResponse;
import com.orvion.sales.application.dto.response.OpportunityResponse;
import com.orvion.sales.application.mapper.SalesMapper;
import com.orvion.sales.domain.model.Lead;
import com.orvion.sales.domain.model.Opportunity;
import com.orvion.sales.domain.model.enums.LeadSource;
import com.orvion.sales.domain.model.event.LeadConvertedEvent;
import com.orvion.sales.domain.repository.LeadRepository;
import com.orvion.sales.domain.repository.OpportunityRepository;
import io.micrometer.core.instrument.Counter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LeadUseCase {
    private final LeadRepository leadRepository;
    private final OpportunityRepository opportunityRepository;
    private final SalesMapper mapper;
    private final Counter leadsCreatedCounter;
    private final RabbitTemplate rabbitTemplate;

    public LeadUseCase(LeadRepository leadRepository, OpportunityRepository opportunityRepository,
                       SalesMapper mapper, Counter leadsCreatedCounter, RabbitTemplate rabbitTemplate) {
        this.leadRepository = leadRepository;
        this.opportunityRepository = opportunityRepository;
        this.mapper = mapper;
        this.leadsCreatedCounter = leadsCreatedCounter;
        this.rabbitTemplate = rabbitTemplate;
    }

    @CacheEvict(value = "leads", allEntries = true)
    public LeadResponse createLead(String tenantId, CreateLeadRequest request) {
        LeadSource source;
        try {
            source = LeadSource.valueOf(request.getSource());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("INVALID_LEAD_SOURCE", "Invalid lead source: " + request.getSource());
        }
        Lead lead = new Lead(tenantId, request.getFirstName(), request.getLastName(),
            request.getEmail(), request.getPhone(), request.getCompany(), source, request.getAssignedTo());
        lead.setNotes(request.getNotes());
        lead = leadRepository.save(lead);
        leadsCreatedCounter.increment();
        return mapper.toLeadResponse(lead);
    }

    @CacheEvict(value = "leads", allEntries = true)
    public LeadResponse qualifyLead(String tenantId, UUID leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId.toString()));
        if (!lead.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Lead does not belong to tenant");
        lead.qualify();
        lead = leadRepository.save(lead);
        return mapper.toLeadResponse(lead);
    }

    @CacheEvict(value = "leads", allEntries = true)
    public LeadResponse disqualifyLead(String tenantId, UUID leadId, String reason) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId.toString()));
        if (!lead.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Lead does not belong to tenant");
        lead.disqualify(reason);
        lead = leadRepository.save(lead);
        return mapper.toLeadResponse(lead);
    }

    @CacheEvict(value = {"leads", "opportunities"}, allEntries = true)
    public OpportunityResponse convertToOpportunity(String tenantId, UUID leadId, String title) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId.toString()));
        if (!lead.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Lead does not belong to tenant");
        Opportunity opp = lead.convertToOpportunity(title, lead.getAssignedTo());
        opp = opportunityRepository.save(opp);
        leadRepository.save(lead);

        LeadConvertedEvent event = new LeadConvertedEvent(UUID.randomUUID(), leadId, opp.getId(), tenantId,
            lead.getFirstName() + " " + lead.getLastName());
        rabbitTemplate.convertAndSend("orvion.sales.exchange", "orvion.sales.lead.converted", event);

        return mapper.toOpportunityResponse(opp);
    }

    @Cacheable(value = "leads", key = "#tenantId + ':' + #leadId")
    @Transactional(readOnly = true)
    public LeadResponse getLeadById(String tenantId, UUID leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId.toString()));
        if (!lead.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Lead does not belong to tenant");
        return mapper.toLeadResponse(lead);
    }

    @Transactional(readOnly = true)
    public List<LeadResponse> getLeads(String tenantId) {
        return mapper.toLeadResponseList(leadRepository.findAllByTenantId(tenantId));
    }
}
