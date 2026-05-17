package com.orvion.finance.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.finance.application.dto.request.CreateJournalEntryLineRequest;
import com.orvion.finance.application.dto.request.CreateJournalEntryRequest;
import com.orvion.finance.application.dto.response.JournalEntryResponse;
import com.orvion.finance.application.mapper.JournalEntryMapper;
import com.orvion.finance.domain.event.JournalEntryPostedEvent;
import com.orvion.finance.domain.model.Account;
import com.orvion.finance.domain.model.JournalEntry;
import com.orvion.finance.domain.model.JournalEntryLine;
import com.orvion.finance.domain.model.enums.DebitCredit;
import com.orvion.finance.domain.model.vo.FiscalPeriod;
import com.orvion.finance.domain.model.vo.Money;
import com.orvion.finance.domain.repository.AccountRepository;
import com.orvion.finance.domain.repository.JournalEntryRepository;
import com.orvion.finance.infrastructure.messaging.FinanceEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class JournalEntryUseCase {

    private static final Logger log = LoggerFactory.getLogger(JournalEntryUseCase.class);

    private final JournalEntryRepository journalEntryRepository;
    private final AccountRepository accountRepository;
    private final JournalEntryMapper journalEntryMapper;
    private final FinanceEventPublisher eventPublisher;

    public JournalEntryUseCase(JournalEntryRepository journalEntryRepository,
                                AccountRepository accountRepository,
                                JournalEntryMapper journalEntryMapper,
                                FinanceEventPublisher eventPublisher) {
        this.journalEntryRepository = journalEntryRepository;
        this.accountRepository = accountRepository;
        this.journalEntryMapper = journalEntryMapper;
        this.eventPublisher = eventPublisher;
    }

    public JournalEntryResponse createJournalEntry(String tenantId, String userId, CreateJournalEntryRequest request) {
        FiscalPeriod period = new FiscalPeriod(request.getYear() != 0 ? request.getYear() : java.time.Year.now().getValue(),
            request.getMonth() != 0 ? request.getMonth() : java.time.LocalDate.now().getMonthValue());

        JournalEntry entry = new JournalEntry(tenantId, request.getReference(),
            request.getDescription(), period, request.getEntryDate() != null ? request.getEntryDate() : Instant.now());

        for (CreateJournalEntryLineRequest lineReq : request.getLines()) {
            Account account = accountRepository.findByTenantIdAndCode(tenantId, lineReq.getAccountCode())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "code", lineReq.getAccountCode()));

            Money amount = new Money(lineReq.getAmount(),
                lineReq.getCurrency() != null ? lineReq.getCurrency() : "IDR");

            JournalEntryLine line = new JournalEntryLine(account.getId(), account.getCode(),
                account.getName(), DebitCredit.valueOf(lineReq.getSide()), amount, lineReq.getDescription());
            entry.addLine(line);
        }

        entry = journalEntryRepository.save(entry);
        return journalEntryMapper.toResponse(entry);
    }

    public JournalEntryResponse postJournalEntry(String tenantId, UUID entryId) {
        JournalEntry entry = journalEntryRepository.findById(entryId)
            .orElseThrow(() -> new ResourceNotFoundException("JournalEntry", "id", entryId.toString()));

        if (!entry.getTenantId().equals(tenantId)) {
            throw new BusinessException("TENANT_MISMATCH", "Journal entry does not belong to tenant");
        }

        entry.post();

        for (JournalEntryLine line : entry.getLines()) {
            Account account = accountRepository.findById(line.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", line.getAccountId().toString()));

            if (line.getSide() == DebitCredit.DEBIT) {
                account.debit(line.getAmount());
            } else {
                account.credit(line.getAmount());
            }
            accountRepository.save(account);
        }

        entry = journalEntryRepository.save(entry);

        FiscalPeriod period = entry.getPeriod();
        JournalEntryPostedEvent event = new JournalEntryPostedEvent(
            entry.getId().toString(), entry.getReference(), period, tenantId);
        eventPublisher.publishEvent(event.getEventType(), event.getAggregateType(),
            event.getAggregateId(), tenantId, serializeEvent(event), "orvion.finance.journal.posted");

        log.info("Journal entry posted: id={}, reference={}, tenant={}", entry.getId(), entry.getReference(), tenantId);
        return journalEntryMapper.toResponse(entry);
    }

    public JournalEntryResponse reverseJournalEntry(String tenantId, UUID entryId, String reason) {
        JournalEntry entry = journalEntryRepository.findById(entryId)
            .orElseThrow(() -> new ResourceNotFoundException("JournalEntry", "id", entryId.toString()));

        if (!entry.getTenantId().equals(tenantId)) {
            throw new BusinessException("TENANT_MISMATCH", "Journal entry does not belong to tenant");
        }

        JournalEntry reversal = entry.reverse(reason);

        for (JournalEntryLine line : reversal.getLines()) {
            Account account = accountRepository.findById(line.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", line.getAccountId().toString()));

            if (line.getSide() == DebitCredit.DEBIT) {
                account.debit(line.getAmount());
            } else {
                account.credit(line.getAmount());
            }
            accountRepository.save(account);
        }

        journalEntryRepository.save(entry);
        reversal = journalEntryRepository.save(reversal);

        log.info("Journal entry reversed: original={}, reversal={}, reason={}",
            entry.getId(), reversal.getId(), reason);
        return journalEntryMapper.toResponse(entry);
    }

    @Transactional(readOnly = true)
    public List<JournalEntryResponse> getJournalEntriesByPeriod(String tenantId, int year, int month) {
        List<JournalEntry> entries = journalEntryRepository.findByTenantIdAndPeriod(tenantId, year, month);
        return journalEntryMapper.toResponseList(entries);
    }

    private String serializeEvent(Object event) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                .writeValueAsString(event);
        } catch (Exception e) {
            log.error("Failed to serialize event: {}", e.getMessage());
            return "{}";
        }
    }
}
