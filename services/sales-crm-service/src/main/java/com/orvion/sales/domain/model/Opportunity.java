package com.orvion.sales.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.sales.domain.model.enums.OpportunityStage;
import com.orvion.sales.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "opportunities", indexes = {
    @Index(name = "idx_opp_tenant_stage", columnList = "tenantId, stage")
})
@Getter @Setter @NoArgsConstructor
public class Opportunity extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 255, nullable = false)
    private String title;

    private UUID leadId;

    @Column(length = 100)
    private String accountId;

    @Column(length = 100)
    private String assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private OpportunityStage stage;

    @Column(nullable = false)
    private int probability;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "expected_value_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "expected_value_currency", length = 3))
    })
    private Money expectedValue;

    private Instant expectedCloseDate;

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Activity> activities = new ArrayList<>();

    public Opportunity(String tenantId, String title, UUID leadId, String accountId, String assignedTo) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.title = title;
        this.leadId = leadId;
        this.accountId = accountId;
        this.assignedTo = assignedTo;
        this.stage = OpportunityStage.DISCOVERY;
        this.probability = 10;
        this.expectedValue = Money.zero("IDR");
    }

    public void advanceStage() {
        if (stage == OpportunityStage.CLOSED_WON || stage == OpportunityStage.CLOSED_LOST) {
            throw new BusinessException("OPPORTUNITY_CLOSED", "Cannot advance a closed opportunity");
        }
        switch (stage) {
            case DISCOVERY -> {
                this.stage = OpportunityStage.PROPOSAL;
                this.probability = 40;
            }
            case PROPOSAL -> {
                this.stage = OpportunityStage.NEGOTIATION;
                this.probability = 70;
            }
            case NEGOTIATION -> {
                this.stage = OpportunityStage.CLOSED_WON;
                this.probability = 100;
            }
        }
    }

    public void closeWon(Money actualValue) {
        if (stage == OpportunityStage.CLOSED_LOST) {
            throw new BusinessException("ALREADY_CLOSED_LOST", "Opportunity is already closed lost");
        }
        this.stage = OpportunityStage.CLOSED_WON;
        this.probability = 100;
        if (actualValue != null) {
            this.expectedValue = actualValue;
        }
    }

    public void closeLost(String reason) {
        if (stage == OpportunityStage.CLOSED_WON) {
            throw new BusinessException("ALREADY_CLOSED_WON", "Opportunity is already closed won");
        }
        this.stage = OpportunityStage.CLOSED_LOST;
        this.probability = 0;
    }

    public void addActivity(Activity activity) {
        activity.setOpportunity(this);
        this.activities.add(activity);
    }
}
