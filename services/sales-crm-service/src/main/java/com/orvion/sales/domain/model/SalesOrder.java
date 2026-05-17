package com.orvion.sales.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.sales.domain.model.enums.SalesOrderStatus;
import com.orvion.sales.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales_orders", indexes = {
    @Index(name = "idx_so_tenant_status", columnList = "tenantId, status"),
    @Index(name = "idx_so_tenant_customer", columnList = "tenantId, customerId")
})
@Getter @Setter @NoArgsConstructor
public class SalesOrder extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 20, unique = true, nullable = false)
    private String orderNumber;

    @Column(length = 100, nullable = false)
    private String customerId;

    @Column(length = 100)
    private String assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private SalesOrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SalesOrderLine> lines = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "total_currency", length = 3))
    })
    private Money totalAmount;

    private Instant orderDate;

    private Instant expectedDelivery;

    public SalesOrder(String tenantId, String customerId, String assignedTo, List<SalesOrderLine> lines) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.orderNumber = generateOrderNumber();
        this.customerId = customerId;
        this.assignedTo = assignedTo;
        this.status = SalesOrderStatus.DRAFT;
        this.orderDate = Instant.now();
        this.totalAmount = Money.zero("IDR");
        lines.forEach(this::addLine);
    }

    private String generateOrderNumber() {
        return "SO-" + String.format("%04d", (int)(Math.random() * 10000));
    }

    public void addLine(SalesOrderLine line) {
        line.setOrder(this);
        this.lines.add(line);
        recalculateTotal();
    }

    private void recalculateTotal() {
        Money total = Money.zero("IDR");
        for (SalesOrderLine line : lines) {
            total = total.add(line.getLineTotal());
        }
        this.totalAmount = total;
    }

    public void confirm() {
        if (status != SalesOrderStatus.DRAFT) {
            throw new BusinessException("INVALID_STATUS", "Only DRAFT orders can be confirmed");
        }
        if (lines.isEmpty()) {
            throw new BusinessException("EMPTY_ORDER", "Cannot confirm an order with no lines");
        }
        this.status = SalesOrderStatus.CONFIRMED;
    }

    public void ship() {
        if (status != SalesOrderStatus.CONFIRMED && status != SalesOrderStatus.PROCESSING) {
            throw new BusinessException("INVALID_STATUS", "Order must be CONFIRMED or PROCESSING to ship");
        }
        this.status = SalesOrderStatus.SHIPPED;
    }

    public void deliver() {
        if (status != SalesOrderStatus.SHIPPED) {
            throw new BusinessException("INVALID_STATUS", "Order must be SHIPPED to deliver");
        }
        this.status = SalesOrderStatus.DELIVERED;
    }

    public void cancel(String reason) {
        if (status == SalesOrderStatus.DELIVERED || status == SalesOrderStatus.CANCELLED) {
            throw new BusinessException("INVALID_STATUS", "Cannot cancel a delivered or already cancelled order");
        }
        this.status = SalesOrderStatus.CANCELLED;
    }
}
