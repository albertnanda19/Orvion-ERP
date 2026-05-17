package com.orvion.finance.application.mapper;

import com.orvion.finance.application.dto.response.AccountResponse;
import com.orvion.finance.domain.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "balance", source = "currentBalance.amount")
    @Mapping(target = "currency", source = "currentBalance.currencyCode")
    @Mapping(target = "balanceType", expression = "java(account.getBalanceType().name())")
    @Mapping(target = "childCount", ignore = true)
    AccountResponse toResponse(Account account);

    List<AccountResponse> toResponseList(List<Account> accounts);
}
