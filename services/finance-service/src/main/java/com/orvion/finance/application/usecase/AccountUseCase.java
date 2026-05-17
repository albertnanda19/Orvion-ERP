package com.orvion.finance.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.finance.application.dto.request.CreateAccountRequest;
import com.orvion.finance.application.dto.response.AccountResponse;
import com.orvion.finance.application.mapper.AccountMapper;
import com.orvion.finance.domain.model.Account;
import com.orvion.finance.domain.model.enums.AccountType;
import com.orvion.finance.domain.model.vo.AccountCode;
import com.orvion.finance.domain.repository.AccountRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AccountUseCase {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountUseCase(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @CacheEvict(value = "accounts", allEntries = true)
    public AccountResponse createAccount(String tenantId, CreateAccountRequest request) {
        if (accountRepository.existsByTenantIdAndCode(tenantId, request.getCode())) {
            throw new BusinessException("DUPLICATE_ACCOUNT_CODE",
                "Account with code " + request.getCode() + " already exists");
        }

        AccountCode accountCode = new AccountCode(request.getCode());
        AccountType type = AccountType.valueOf(request.getType());
        UUID parentId = request.getParentAccountId() != null
            ? UUID.fromString(request.getParentAccountId()) : null;

        Account account = new Account(tenantId, accountCode, request.getName(),
            type, request.getDescription(), parentId,
            parentId != null ? 2 : 1);

        account = accountRepository.save(account);
        return accountMapper.toResponse(account);
    }

    @CacheEvict(value = "accounts", allEntries = true)
    public AccountResponse updateAccount(String tenantId, UUID accountId, CreateAccountRequest request) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId.toString()));

        if (!account.getTenantId().equals(tenantId)) {
            throw new BusinessException("TENANT_MISMATCH", "Account does not belong to tenant");
        }

        account.setName(request.getName());
        account.setDescription(request.getDescription());
        account = accountRepository.save(account);
        return accountMapper.toResponse(account);
    }

    @CacheEvict(value = "accounts", allEntries = true)
    public void deactivateAccount(String tenantId, UUID accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId.toString()));
        if (!account.getTenantId().equals(tenantId)) {
            throw new BusinessException("TENANT_MISMATCH", "Account does not belong to tenant");
        }
        account.deactivate();
        accountRepository.save(account);
    }

    @Cacheable(value = "accounts", key = "#tenantId + ':' + #accountId")
    public AccountResponse getAccountById(String tenantId, UUID accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId.toString()));
        if (!account.getTenantId().equals(tenantId)) {
            throw new BusinessException("TENANT_MISMATCH", "Account does not belong to tenant");
        }
        return accountMapper.toResponse(account);
    }

    public List<AccountResponse> getChartOfAccounts(String tenantId) {
        List<Account> accounts = accountRepository.findAllByTenantIdAndActiveTrue(tenantId);
        return accountMapper.toResponseList(accounts);
    }
}
