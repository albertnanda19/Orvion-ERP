package com.orvion.reporting.domain.model;

import com.orvion.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "reporting_inventory_facts", indexes = {
    @Index(name = "idx_inv_fact_tenant_period", columnList = "tenantId, period")
})
@Getter @Setter @NoArgsConstructor
public class ReportInventoryFact extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 10, nullable = false)
    private String period;

    @Column(name = "total_products")
    private Long totalProducts;

    @Column(name = "total_stock_value", precision = 18, scale = 2)
    private BigDecimal totalStockValue;

    @Column(name = "low_stock_count")
    private Long lowStockCount;

    @Column(name = "turnover_rate", precision = 10, scale = 4)
    private BigDecimal turnoverRate;
}
