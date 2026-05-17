package com.orvion.finance.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.finance.domain.model.enums.DebitCredit;
import com.orvion.finance.domain.model.enums.JournalEntryStatus;
import com.orvion.finance.domain.model.vo.FiscalPeriod;
import com.orvion.finance.domain.model.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JournalEntryTest {

    private JournalEntry entry;
    private UUID debitAccountId;
    private UUID creditAccountId;

    @BeforeEach
    void setUp() {
        entry = new JournalEntry("tenant1", "JE-001",
            "Test journal entry", new FiscalPeriod(2024, 6), Instant.now());
        debitAccountId = UUID.randomUUID();
        creditAccountId = UUID.randomUUID();
    }

    @Test
    void testCreateDraftEntry() {
        assertEquals(JournalEntryStatus.DRAFT, entry.getStatus());
        assertTrue(entry.getLines().isEmpty());
    }

    @Test
    void testAddLine() {
        JournalEntryLine debit = new JournalEntryLine(debitAccountId, "1100",
            "Cash", DebitCredit.DEBIT, new Money(new BigDecimal("1000000.0000"), "IDR"), "Payment");
        entry.addLine(debit);

        assertEquals(1, entry.getLines().size());
        assertEquals(1, entry.getLines().get(0).getLineNumber());
    }

    @Test
    void testPostSucceedsWhenBalanced() {
        entry.addLine(new JournalEntryLine(debitAccountId, "1100", "Cash",
            DebitCredit.DEBIT, new Money(new BigDecimal("1000000.0000"), "IDR"), "Payment"));
        entry.addLine(new JournalEntryLine(creditAccountId, "4100", "Revenue",
            DebitCredit.CREDIT, new Money(new BigDecimal("1000000.0000"), "IDR"), "Revenue"));

        entry.post();
        assertEquals(JournalEntryStatus.POSTED, entry.getStatus());
    }

    @Test
    void testPostFailsWhenUnbalanced() {
        entry.addLine(new JournalEntryLine(debitAccountId, "1100", "Cash",
            DebitCredit.DEBIT, new Money(new BigDecimal("1000000.0000"), "IDR"), "Payment"));
        entry.addLine(new JournalEntryLine(creditAccountId, "4100", "Revenue",
            DebitCredit.CREDIT, new Money(new BigDecimal("500000.0000"), "IDR"), "Revenue"));

        assertThrows(BusinessException.class, () -> entry.post());
        assertEquals(JournalEntryStatus.DRAFT, entry.getStatus());
    }

    @Test
    void testPostFailsWhenNoLines() {
        assertThrows(BusinessException.class, () -> entry.post());
    }

    @Test
    void testReverse() {
        entry.addLine(new JournalEntryLine(debitAccountId, "1100", "Cash",
            DebitCredit.DEBIT, new Money(new BigDecimal("1000000.0000"), "IDR"), "Payment"));
        entry.addLine(new JournalEntryLine(creditAccountId, "4100", "Revenue",
            DebitCredit.CREDIT, new Money(new BigDecimal("1000000.0000"), "IDR"), "Revenue"));
        entry.post();

        JournalEntry reversal = entry.reverse("Correction of error");

        assertEquals(JournalEntryStatus.REVERSED, entry.getStatus());
        assertNotNull(reversal);
        assertEquals(2, reversal.getLines().size());

        assertEquals(DebitCredit.CREDIT, reversal.getLines().get(0).getSide());
        assertEquals(DebitCredit.DEBIT, reversal.getLines().get(1).getSide());
    }

    @Test
    void testReverseFailsWhenNotPosted() {
        assertThrows(BusinessException.class, () -> entry.reverse("Test"));
    }

    @Test
    void testGetTotalDebitsAndCredits() {
        entry.addLine(new JournalEntryLine(debitAccountId, "1100", "Cash",
            DebitCredit.DEBIT, new Money(new BigDecimal("2000000.0000"), "IDR"), "Payment"));
        entry.addLine(new JournalEntryLine(debitAccountId, "1200", "AR",
            DebitCredit.DEBIT, new Money(new BigDecimal("500000.0000"), "IDR"), "AR"));
        entry.addLine(new JournalEntryLine(creditAccountId, "4100", "Revenue",
            DebitCredit.CREDIT, new Money(new BigDecimal("2500000.0000"), "IDR"), "Revenue"));

        assertEquals(new BigDecimal("2500000.0000"), entry.getTotalDebits().getAmount());
        assertEquals(new BigDecimal("2500000.0000"), entry.getTotalCredits().getAmount());
        assertTrue(entry.isBalanced());
    }

    @Test
    void testModifyPostedEntryFails() {
        entry.addLine(new JournalEntryLine(debitAccountId, "1100", "Cash",
            DebitCredit.DEBIT, new Money(new BigDecimal("1000000.0000"), "IDR"), "Payment"));
        entry.addLine(new JournalEntryLine(creditAccountId, "4100", "Revenue",
            DebitCredit.CREDIT, new Money(new BigDecimal("1000000.0000"), "IDR"), "Revenue"));
        entry.post();

        assertThrows(BusinessException.class, () ->
            entry.addLine(new JournalEntryLine(UUID.randomUUID(), "9999", "Test",
                DebitCredit.DEBIT, Money.zero("IDR"), "Test")));
    }
}
