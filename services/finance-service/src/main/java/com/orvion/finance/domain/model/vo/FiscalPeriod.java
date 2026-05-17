package com.orvion.finance.domain.model.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;

@Getter
@EqualsAndHashCode
public class FiscalPeriod {

    private final int year;
    private final int month;
    private final YearMonth yearMonth;

    public FiscalPeriod(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12: " + month);
        }
        this.year = year;
        this.month = month;
        this.yearMonth = YearMonth.of(year, month);
    }

    public FiscalPeriod next() {
        YearMonth next = yearMonth.plusMonths(1);
        return new FiscalPeriod(next.getYear(), next.getMonthValue());
    }

    public FiscalPeriod previous() {
        YearMonth prev = yearMonth.minusMonths(1);
        return new FiscalPeriod(prev.getYear(), prev.getMonthValue());
    }

    public boolean isWithin(Instant instant) {
        YearMonth instantYm = YearMonth.from(instant.atZone(ZoneOffset.UTC));
        return yearMonth.equals(instantYm);
    }

    public String toLabel() {
        return String.format("%04d-%02d", year, month);
    }

    public Instant getStartInstant() {
        return yearMonth.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    public Instant getEndInstant() {
        return yearMonth.atEndOfMonth().atTime(23, 59, 59, 999999999)
            .atZone(ZoneOffset.UTC).toInstant();
    }

    public static FiscalPeriod from(Instant instant) {
        YearMonth ym = YearMonth.from(instant.atZone(ZoneOffset.UTC));
        return new FiscalPeriod(ym.getYear(), ym.getMonthValue());
    }

    @Override
    public String toString() {
        return toLabel();
    }
}
