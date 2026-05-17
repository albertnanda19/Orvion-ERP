package com.orvion.finance.domain.model;

import com.orvion.finance.domain.model.vo.FiscalPeriod;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class FiscalPeriodTest {

    @Test
    void testValidPeriod() {
        FiscalPeriod period = new FiscalPeriod(2024, 6);
        assertEquals(2024, period.getYear());
        assertEquals(6, period.getMonth());
        assertEquals("2024-06", period.toLabel());
    }

    @Test
    void testInvalidMonth() {
        assertThrows(IllegalArgumentException.class, () -> new FiscalPeriod(2024, 0));
        assertThrows(IllegalArgumentException.class, () -> new FiscalPeriod(2024, 13));
    }

    @Test
    void testNext() {
        FiscalPeriod period = new FiscalPeriod(2024, 12);
        FiscalPeriod next = period.next();
        assertEquals(2025, next.getYear());
        assertEquals(1, next.getMonth());
    }

    @Test
    void testPrevious() {
        FiscalPeriod period = new FiscalPeriod(2024, 1);
        FiscalPeriod prev = period.previous();
        assertEquals(2023, prev.getYear());
        assertEquals(12, prev.getMonth());
    }

    @Test
    void testIsWithin() {
        FiscalPeriod period = new FiscalPeriod(2024, 6);
        Instant june15 = LocalDate.of(2024, 6, 15).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant july1 = LocalDate.of(2024, 7, 1).atStartOfDay(ZoneOffset.UTC).toInstant();
        assertTrue(period.isWithin(june15));
        assertFalse(period.isWithin(july1));
    }

    @Test
    void testFromInstant() {
        Instant now = LocalDate.of(2024, 6, 15).atStartOfDay(ZoneOffset.UTC).toInstant();
        FiscalPeriod period = FiscalPeriod.from(now);
        assertEquals(2024, period.getYear());
        assertEquals(6, period.getMonth());
    }

    @Test
    void testDateRange() {
        FiscalPeriod period = new FiscalPeriod(2024, 6);
        Instant start = period.getStartInstant();
        Instant end = period.getEndInstant();
        assertTrue(start.isBefore(end));
        assertEquals("2024-06-01", start.atZone(ZoneOffset.UTC).toLocalDate().toString());
    }
}
