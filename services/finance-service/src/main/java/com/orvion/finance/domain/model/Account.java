package com.orvion.finance.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.finance.domain.model.enums.AccountType;
import com.orvion.finance.domain.model.enums.DebitCredit;
import com.orvion.finance.domain.model.vo.AccountCode;
import com.orvion.finance.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "accounts", indexes = {
    @Index(name = "idx_accounts_tenant_type", columnList = "tenantId, type"),
    @Index(name = "idx_accounts_tenant_code", columnList = "tenantId, code", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class Account extends Auditable {

    @Id
    private UUID id;

    @Column(name = "tenant_id", length = 50, nullable = false)
    private String tenantId;

    @Column(name = "code", length = 20, nullable = false)
    private String code;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private AccountType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "parent_account_id")
    private UUID parentAccountId;

    @Column(name = "level", nullable = false)
    private int level = 1;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "current_balance_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "current_balance_currency", length = 3))
    })
    private Money currentBalance;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "opening_balance_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "opening_balance_currency", length = 3))
    })
    private Money openingBalance;

    public Account(String tenantId, AccountCode accountCode, String name, AccountType type,
                   String description, UUID parentAccountId, int level) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.code = accountCode.getCode();
        this.name = name;
        this.type = type;
        this.description = description;
        this.parentAccountId = parentAccountId;
        this.level = level;
        this.currentBalance = Money.zero("IDR");
        this.openingBalance = Money.zero("IDR");
        this.active = true;
    }

    public void debit(Money amount) {
        if (isDebitNormal()) {
            this.currentBalance = this.currentBalance.add(amount);
        } else {
            this.currentBalance = this.currentBalance.subtract(amount);
        }
    }

    public void credit(Money amount) {
        if (isCreditNormal()) {
            this.currentBalance = this.currentBalance.add(amount);
        } else {
            this.currentBalance = this.currentBalance.subtract(amount);
        }
    }

    public void deactivate() {
        if (!this.active) {
            throw new BusinessException("ACCOUNT_ALREADY_INACTIVE", "Account is already inactive");
        }
        if (!this.currentBalance.isZero()) {
            throw new BusinessException("ACCOUNT_HAS_BALANCE",
                "Cannot deactivate account with non-zero balance: " + this.currentBalance);
        }
        this.active = false;
    }

    public DebitCredit getBalanceType() {
        return isDebitNormal() ? DebitCredit.DEBIT : DebitCredit.CREDIT;
    }

    public boolean isDebitNormal() {
        return type == AccountType.ASSET || type == AccountType.EXPENSE;
    }

    public boolean isCreditNormal() {
        return type == AccountType.LIABILITY || type == AccountType.EQUITY || type == AccountType.REVENUE;
    }

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
