package com.orvion.reporting.domain.model;

import com.orvion.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "reporting_sales_facts", indexes = {
    @Index(name = "idx_sales_fact_tenant_period", columnList = "tenantId, period")
})
@Getter @Setter @NoArgsConstructor
public class ReportSalesFact extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 10, nullable = false)
    private String period;

    @Column(name = "total_orders")
    private Long totalOrders;

    @Column(name = "total_revenue", precision = 18, scale = 2)
    private BigDecimal totalRevenue;

    @Column(name = "conversion_rate", precision = 10, scale = 4)
    private BigDecimal conversionRate;

    @Column(name = "avg_order_value", precision = 18, scale = 2)
    private BigDecimal avgOrderValue;
}
