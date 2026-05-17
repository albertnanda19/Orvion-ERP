package com.orvion.sales.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.sales.domain.model.enums.LeadSource;
import com.orvion.sales.domain.model.enums.LeadStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "leads", indexes = {
    @Index(name = "idx_leads_tenant_status", columnList = "tenantId, status"),
    @Index(name = "idx_leads_tenant_source", columnList = "tenantId, source")
})
@Getter @Setter @NoArgsConstructor
public class Lead extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 100, nullable = false)
    private String firstName;

    @Column(length = 100, nullable = false)
    private String lastName;

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(length = 200)
    private String company;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private LeadSource source;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private LeadStatus status;

    @Column(length = 100)
    private String assignedTo;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Lead(String tenantId, String firstName, String lastName, String email, String phone,
                String company, LeadSource source, String assignedTo) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.source = source;
        this.status = LeadStatus.NEW;
        this.assignedTo = assignedTo;
    }

    public void qualify() {
        if (status == LeadStatus.DISQUALIFIED) {
            throw new BusinessException("LEAD_DISQUALIFIED", "Cannot qualify a disqualified lead");
        }
        this.status = LeadStatus.QUALIFIED;
    }

    public void disqualify(String reason) {
        if (status == LeadStatus.DISQUALIFIED) {
            throw new BusinessException("ALREADY_DISQUALIFIED", "Lead is already disqualified");
        }
        this.status = LeadStatus.DISQUALIFIED;
        this.notes = reason;
    }

    public Opportunity convertToOpportunity(String title, String assignedTo) {
        if (status != LeadStatus.QUALIFIED) {
            throw new BusinessException("LEAD_NOT_QUALIFIED", "Lead must be qualified before conversion");
        }
        return new Opportunity(tenantId, title, id, null, assignedTo);
    }
}
