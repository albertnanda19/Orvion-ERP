package com.orvion.sales.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.sales.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "sales_order_lines")
@Getter @Setter @NoArgsConstructor
public class SalesOrderLine extends Auditable {
    @Id
    private UUID id;

    @Column(length = 100, nullable = false)
    private String productId;

    @Column(length = 255)
    private String productName;

    @Column(length = 100)
    private String sku;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal quantity;

    @Column(precision = 19, scale = 4)
    private BigDecimal reservedQuantity;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "unit_price_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "unit_price_currency", length = 3))
    })
    private Money unitPrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "line_total_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "line_total_currency", length = 3))
    })
    private Money lineTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private SalesOrder order;

    public SalesOrderLine(String productId, String productName, String sku,
                          BigDecimal quantity, Money unitPrice) {
        this.id = UUID.randomUUID();
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.quantity = quantity;
        this.reservedQuantity = BigDecimal.ZERO;
        this.unitPrice = unitPrice;
        this.lineTotal = unitPrice.multiply(quantity);
    }

    public void reserve(BigDecimal quantity) {
        this.reservedQuantity = quantity;
    }
}
