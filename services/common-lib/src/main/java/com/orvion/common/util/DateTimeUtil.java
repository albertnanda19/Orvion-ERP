package com.orvion.common.util;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

public final class DateTimeUtil {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");

    private DateTimeUtil() {
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now(DEFAULT_ZONE);
    }

    public static ZonedDateTime toZone(Instant instant, String zoneId) {
        return ZonedDateTime.ofInstant(instant, ZoneId.of(zoneId));
    }

    public static ZonedDateTime toZone(Instant instant, ZoneId zoneId) {
        return ZonedDateTime.ofInstant(instant, zoneId);
    }

    public static LocalDate toLocalDate(Instant instant) {
        return LocalDate.ofInstant(instant, DEFAULT_ZONE);
    }

    public static Instant startOfFiscalYear(int year) {
        return LocalDate.of(year, 1, 1).atStartOfDay(DEFAULT_ZONE).toInstant();
    }

    public static Instant endOfFiscalYear(int year) {
        return LocalDate.of(year, 12, 31).atTime(23, 59, 59, 999_999_999)
                .atZone(DEFAULT_ZONE).toInstant();
    }

    public static Instant startOfMonth(YearMonth yearMonth) {
        return yearMonth.atDay(1).atStartOfDay(DEFAULT_ZONE).toInstant();
    }

    public static Instant endOfMonth(YearMonth yearMonth) {
        return yearMonth.atEndOfMonth().atTime(23, 59, 59, 999_999_999)
                .atZone(DEFAULT_ZONE).toInstant();
    }

    public static Instant startOfQuarter(int year, int quarter) {
        int month = (quarter - 1) * 3 + 1;
        return LocalDate.of(year, month, 1).atStartOfDay(DEFAULT_ZONE).toInstant();
    }

    public static Instant endOfQuarter(int year, int quarter) {
        int month = quarter * 3;
        return LocalDate.of(year, month, 1)
                .with(TemporalAdjusters.lastDayOfMonth())
                .atTime(23, 59, 59, 999_999_999)
                .atZone(DEFAULT_ZONE).toInstant();
    }

    public static boolean isWithinFiscalPeriod(Instant date, int fiscalYear) {
        LocalDate localDate = toLocalDate(date);
        return localDate.getYear() == fiscalYear;
    }
}
