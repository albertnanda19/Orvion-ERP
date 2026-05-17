package com.orvion.finance.infrastructure.persistence.repository;

import com.orvion.finance.domain.model.Account;
import com.orvion.finance.domain.model.enums.AccountType;
import com.orvion.finance.domain.repository.AccountRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountJpaRepository extends JpaRepository<Account, UUID>, AccountRepository {

    @Override
    Optional<Account> findById(UUID id);

    @Override
    @Query("SELECT a FROM Account a WHERE a.tenantId = :tenantId AND a.code = :code")
    Optional<Account> findByTenantIdAndCode(@Param("tenantId") String tenantId, @Param("code") String code);

    @Override
    @Query("SELECT a FROM Account a WHERE a.tenantId = :tenantId AND a.parentAccountId = :parentId")
    List<Account> findAllByTenantIdAndParentAccountId(@Param("tenantId") String tenantId,
                                                      @Param("parentId") UUID parentAccountId);

    @Override
    @Query("SELECT a FROM Account a WHERE a.tenantId = :tenantId AND a.type = :type")
    List<Account> findByTenantIdAndType(@Param("tenantId") String tenantId, @Param("type") AccountType type);

    @Override
    @Query("SELECT a FROM Account a WHERE a.tenantId = :tenantId")
    List<Account> findAllByTenantId(@Param("tenantId") String tenantId);

    @Override
    @Query("SELECT a FROM Account a WHERE a.tenantId = :tenantId AND a.active = true")
    List<Account> findAllByTenantIdAndActiveTrue(@Param("tenantId") String tenantId);

    @Override
    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.tenantId = :tenantId AND a.code = :code")
    boolean existsByTenantIdAndCode(@Param("tenantId") String tenantId, @Param("code") String code);
}
