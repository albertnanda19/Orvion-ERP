package com.orvion.finance.domain.model;

import com.orvion.finance.domain.model.enums.DebitCredit;
import com.orvion.finance.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "journal_entry_lines")
@Getter
@Setter
@NoArgsConstructor
public class JournalEntryLine {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    private JournalEntry journalEntry;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "account_code", length = 20, nullable = false)
    private String accountCode;

    @Column(name = "account_name", length = 200)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "side", length = 10, nullable = false)
    private DebitCredit side;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "amount", precision = 19, scale = 4, nullable = false)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "currency", length = 3))
    })
    private Money amount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    public JournalEntryLine(UUID accountId, String accountCode, String accountName,
                            DebitCredit side, Money amount, String description) {
        this.id = UUID.randomUUID();
        this.accountId = accountId;
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.side = side;
        this.amount = amount;
        this.description = description;
    }
}
