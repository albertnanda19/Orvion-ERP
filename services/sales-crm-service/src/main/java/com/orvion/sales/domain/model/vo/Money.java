package com.orvion.sales.domain.model.vo;

import com.orvion.common.exception.BusinessException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

@Getter
@EqualsAndHashCode
@Embeddable
public class Money {

    public static final int SCALE = 4;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private BigDecimal amount;
    private String currencyCode;

    protected Money() {
    }

    public Money(BigDecimal amount, String currencyCode) {
        if (amount == null) {
            throw new BusinessException("MONEY_NULL", "Amount must not be null");
        }
        try {
            Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("INVALID_CURRENCY", "Invalid currency code: " + currencyCode);
        }
        this.amount = amount.setScale(SCALE, ROUNDING);
        this.currencyCode = currencyCode;
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currencyCode);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currencyCode);
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier), this.currencyCode);
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isZero() { return amount.compareTo(BigDecimal.ZERO) == 0; }

    public boolean isNegative() { return amount.compareTo(BigDecimal.ZERO) < 0; }

    public Money negate() { return new Money(amount.negate(), currencyCode); }

    public Money abs() { return new Money(amount.abs(), currencyCode); }

    public int compareTo(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

    public boolean isGreaterThan(Money other) { return compareTo(other) > 0; }

    public boolean isLessThan(Money other) { return compareTo(other) < 0; }

    private void validateSameCurrency(Money other) {
        if (!this.currencyCode.equals(other.currencyCode)) {
            throw new BusinessException("CURRENCY_MISMATCH",
                "Currency mismatch: " + this.currencyCode + " vs " + other.currencyCode);
        }
    }

    public static Money zero(String currencyCode) {
        return new Money(BigDecimal.ZERO, currencyCode);
    }

    public static Money of(String amount, String currencyCode) {
        return new Money(new BigDecimal(amount), currencyCode);
    }

    @Override
    public String toString() {
        Currency currency = Currency.getInstance(currencyCode);
        NumberFormat format = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        format.setMinimumFractionDigits(SCALE);
        format.setMaximumFractionDigits(SCALE);
        return currency.getSymbol() + " " + format.format(amount);
    }
}
