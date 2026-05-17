package com.orvion.finance.domain.model.vo;

import com.orvion.common.exception.BusinessException;
import com.orvion.finance.domain.model.enums.AccountType;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
public class AccountCode {

    private static final Pattern CODE_PATTERN = Pattern.compile("^[1-9][0-9]{3,7}$");

    private final String code;
    private final AccountType accountType;

    public AccountCode(String code) {
        if (code == null || !CODE_PATTERN.matcher(code).matches()) {
            throw new BusinessException("INVALID_ACCOUNT_CODE",
                "Account code must be 4-8 digits starting with 1-9: " + code);
        }
        this.code = code;
        this.accountType = deriveType(code.charAt(0));
    }

    private AccountType deriveType(char firstDigit) {
        return switch (firstDigit) {
            case '1' -> AccountType.ASSET;
            case '2' -> AccountType.LIABILITY;
            case '3' -> AccountType.EQUITY;
            case '4' -> AccountType.REVENUE;
            case '5' -> AccountType.EXPENSE;
            default -> throw new BusinessException("INVALID_ACCOUNT_CODE",
                "First digit must be 1-5. Got: " + firstDigit);
        };
    }

    @Override
    public String toString() {
        return code;
    }
}
