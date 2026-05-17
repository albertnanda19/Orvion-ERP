package com.orvion.reporting.domain.model;

import com.orvion.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "reporting_finance_facts", indexes = {
    @Index(name = "idx_fin_fact_tenant_period", columnList = "tenantId, period")
})
@Getter @Setter @NoArgsConstructor
public class ReportFinanceFact extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 10, nullable = false)
    private String period;

    @Column(precision = 18, scale = 2)
    private BigDecimal revenue;

    @Column(precision = 18, scale = 2)
    private BigDecimal expenses;

    @Column(name = "net_profit", precision = 18, scale = 2)
    private BigDecimal netProfit;

    @Column(name = "gross_margin", precision = 18, scale = 2)
    private BigDecimal grossMargin;

    @Column(name = "invoice_count")
    private Long invoiceCount;
}
