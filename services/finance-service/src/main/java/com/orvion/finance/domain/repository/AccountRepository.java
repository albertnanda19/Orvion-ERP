package com.orvion.finance.domain.repository;

import com.orvion.finance.domain.model.Account;
import com.orvion.finance.domain.model.enums.AccountType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(UUID id);

    Optional<Account> findByTenantIdAndCode(String tenantId, String code);

    List<Account> findAllByTenantIdAndParentAccountId(String tenantId, UUID parentAccountId);

    List<Account> findByTenantIdAndType(String tenantId, AccountType type);

    List<Account> findAllByTenantId(String tenantId);

    List<Account> findAllByTenantIdAndActiveTrue(String tenantId);

    boolean existsByTenantIdAndCode(String tenantId, String code);
}
