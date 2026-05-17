package com.orvion.finance.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.finance.domain.model.enums.DebitCredit;
import com.orvion.finance.domain.model.enums.JournalEntryStatus;
import com.orvion.finance.domain.model.vo.FiscalPeriod;
import com.orvion.finance.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "journal_entries", indexes = {
    @Index(name = "idx_je_tenant_period", columnList = "tenantId, year, month"),
    @Index(name = "idx_je_tenant_status", columnList = "tenantId, status"),
    @Index(name = "idx_je_reference", columnList = "reference")
})
@Getter
@Setter
@NoArgsConstructor
public class JournalEntry extends Auditable {

    @Id
    private UUID id;

    @Column(name = "tenant_id", length = 50, nullable = false)
    private String tenantId;

    @Column(length = 100)
    private String reference;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "month", nullable = false)
    private int month;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private JournalEntryStatus status = JournalEntryStatus.DRAFT;

    @Column(name = "entry_date", nullable = false)
    private Instant entryDate;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lineNumber ASC")
    private List<JournalEntryLine> lines = new ArrayList<>();

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "approved_by")
    private String approvedBy;

    public JournalEntry(String tenantId, String reference, String description,
                        FiscalPeriod period, Instant entryDate) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.reference = reference;
        this.description = description;
        this.year = period.getYear();
        this.month = period.getMonth();
        this.entryDate = entryDate;
        this.status = JournalEntryStatus.DRAFT;
        this.lines = new ArrayList<>();
    }

    public FiscalPeriod getPeriod() {
        return new FiscalPeriod(year, month);
    }

    public void setPeriod(FiscalPeriod period) {
        this.year = period.getYear();
        this.month = period.getMonth();
    }

    public void addLine(JournalEntryLine line) {
        if (this.status != JournalEntryStatus.DRAFT) {
            throw new BusinessException("ENTRY_NOT_DRAFT", "Cannot modify a posted or reversed journal entry");
        }
        line.setJournalEntry(this);
        line.setLineNumber(this.lines.size() + 1);
        this.lines.add(line);
    }

    public void post() {
        if (this.status == JournalEntryStatus.POSTED) {
            throw new BusinessException("ENTRY_ALREADY_POSTED", "Journal entry is already posted");
        }
        if (this.status == JournalEntryStatus.REVERSED) {
            throw new BusinessException("ENTRY_ALREADY_REVERSED", "Journal entry has been reversed");
        }
        if (this.lines.isEmpty()) {
            throw new BusinessException("ENTRY_NO_LINES", "Cannot post a journal entry with no lines");
        }
        if (!isBalanced()) {
            throw new BusinessException("UNBALANCED_ENTRY",
                "Journal entry is not balanced: debits (" + getTotalDebits() + ") != credits (" + getTotalCredits() + ")");
        }
        this.status = JournalEntryStatus.POSTED;
    }

    public JournalEntry reverse(String reason) {
        if (this.status != JournalEntryStatus.POSTED) {
            throw new BusinessException("ENTRY_NOT_POSTED", "Can only reverse a posted journal entry");
        }

        JournalEntry reversal = new JournalEntry(this.tenantId,
            "REV-" + this.reference,
            "Reversal of " + this.id + ": " + reason,
            getPeriod(), Instant.now());

        for (JournalEntryLine line : this.lines) {
            DebitCredit oppositeSide = line.getSide() == DebitCredit.DEBIT ? DebitCredit.CREDIT : DebitCredit.DEBIT;
            JournalEntryLine reversalLine = new JournalEntryLine(line.getAccountId(),
                line.getAccountCode(), line.getAccountName(), oppositeSide, line.getAmount(),
                "Reversal: " + line.getDescription());
            reversal.addLine(reversalLine);
        }

        this.status = JournalEntryStatus.REVERSED;
        return reversal;
    }

    public Money getTotalDebits() {
        return lines.stream()
            .filter(l -> l.getSide() == DebitCredit.DEBIT)
            .map(JournalEntryLine::getAmount)
            .reduce(Money.zero("IDR"), Money::add);
    }

    public Money getTotalCredits() {
        return lines.stream()
            .filter(l -> l.getSide() == DebitCredit.CREDIT)
            .map(JournalEntryLine::getAmount)
            .reduce(Money.zero("IDR"), Money::add);
    }

    public boolean isBalanced() {
        Money debits = getTotalDebits();
        Money credits = getTotalCredits();
        return debits.isZero() && credits.isZero() || debits.compareTo(credits) == 0;
    }

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
