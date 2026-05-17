package com.orvion.sales.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.sales.domain.model.enums.CustomerType;
import com.orvion.sales.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_cust_tenant_type", columnList = "tenantId, customerType"),
    @Index(name = "idx_cust_tenant_code", columnList = "tenantId, code")
})
@Getter @Setter @NoArgsConstructor
public class Customer extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 20, unique = true, nullable = false)
    private String code;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "credit_limit_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "credit_limit_currency", length = 3))
    })
    private Money creditLimit;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "outstanding_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "outstanding_currency", length = 3))
    })
    private Money outstandingBalance;

    @Column(length = 100)
    private String paymentTerms;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private CustomerType customerType;

    public Customer(String tenantId, String name, String email, String phone, String address,
                    CustomerType customerType) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.code = generateCode();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.customerType = customerType;
        this.creditLimit = Money.zero("IDR");
        this.outstandingBalance = Money.zero("IDR");
        this.paymentTerms = "NET30";
    }

    private String generateCode() {
        return "CUST-" + String.format("%05d", (int)(Math.random() * 100000));
    }

    public void setCreditLimit(Money limit) {
        if (limit.isNegative()) {
            throw new BusinessException("INVALID_CREDIT_LIMIT", "Credit limit cannot be negative");
        }
        this.creditLimit = limit;
    }

    public void updateOutstanding(Money amount) {
        this.outstandingBalance = amount;
    }
}
