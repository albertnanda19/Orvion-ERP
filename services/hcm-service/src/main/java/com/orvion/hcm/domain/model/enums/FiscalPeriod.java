package com.orvion.hcm.domain.model.enums;

import java.time.YearMonth;
import java.util.Objects;

public class FiscalPeriod {
    private final int year;
    private final int month;

    public FiscalPeriod(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public static FiscalPeriod current() {
        YearMonth ym = YearMonth.now();
        return new FiscalPeriod(ym.getYear(), ym.getMonthValue());
    }

    public int getYear() { return year; }
    public int getMonth() { return month; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiscalPeriod that)) return false;
        return year == that.year && month == that.month;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month);
    }

    @Override
    public String toString() {
        return String.format("%04d-%02d", year, month);
    }
}
