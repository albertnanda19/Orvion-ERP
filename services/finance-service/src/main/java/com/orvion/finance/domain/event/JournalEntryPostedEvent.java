package com.orvion.finance.domain.event;

import com.orvion.common.event.DomainEvent;
import com.orvion.finance.domain.model.vo.FiscalPeriod;
import lombok.Getter;

@Getter
public class JournalEntryPostedEvent extends DomainEvent {

    private final String journalEntryId;
    private final String reference;
    private final FiscalPeriod period;

    public JournalEntryPostedEvent(String journalEntryId, String reference,
                                   FiscalPeriod period, String tenantId) {
        super("JOURNAL_ENTRY_POSTED", tenantId, journalEntryId, "JOURNAL_ENTRY");
        this.journalEntryId = journalEntryId;
        this.reference = reference;
        this.period = period;
    }
}
