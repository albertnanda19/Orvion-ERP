package com.orvion.finance.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.finance.domain.model.enums.AccountType;
import com.orvion.finance.domain.model.vo.AccountCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountCodeTest {

    @Test
    void testValidCodes() {
        assertEquals(AccountType.ASSET, new AccountCode("1100").getAccountType());
        assertEquals(AccountType.LIABILITY, new AccountCode("2100").getAccountType());
        assertEquals(AccountType.EQUITY, new AccountCode("3100").getAccountType());
        assertEquals(AccountType.REVENUE, new AccountCode("4100").getAccountType());
        assertEquals(AccountType.EXPENSE, new AccountCode("5100").getAccountType());
    }

    @Test
    void testInvalidCodes() {
        assertThrows(BusinessException.class, () -> new AccountCode("0123"));
        assertThrows(BusinessException.class, () -> new AccountCode("123"));
        assertThrows(BusinessException.class, () -> new AccountCode("123456789"));
        assertThrows(BusinessException.class, () -> new AccountCode(""));
        assertThrows(BusinessException.class, () -> new AccountCode(null));
    }

    @Test
    void testBoundaryLengths() {
        assertDoesNotThrow(() -> new AccountCode("1000"));
        assertDoesNotThrow(() -> new AccountCode("12345678"));
        assertThrows(BusinessException.class, () -> new AccountCode("999"));
        assertThrows(BusinessException.class, () -> new AccountCode("123456789"));
    }
}
