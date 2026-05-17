package com.orvion.finance.application.usecase;

import com.orvion.finance.application.dto.response.*;
import com.orvion.finance.domain.model.Account;
import com.orvion.finance.domain.model.JournalEntry;
import com.orvion.finance.domain.model.JournalEntryLine;
import com.orvion.finance.domain.model.enums.AccountType;
import com.orvion.finance.domain.model.enums.DebitCredit;
import com.orvion.finance.domain.model.vo.FiscalPeriod;
import com.orvion.finance.domain.repository.AccountRepository;
import com.orvion.finance.domain.repository.JournalEntryRepository;
import com.orvion.finance.domain.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FinancialReportUseCase {

    private static final Logger log = LoggerFactory.getLogger(FinancialReportUseCase.class);

    private final AccountRepository accountRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final InvoiceRepository invoiceRepository;

    public FinancialReportUseCase(AccountRepository accountRepository,
                                   JournalEntryRepository journalEntryRepository,
                                   InvoiceRepository invoiceRepository) {
        this.accountRepository = accountRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public TrialBalanceResponse generateTrialBalance(String tenantId, int year, int month) {
        FiscalPeriod period = new FiscalPeriod(year, month);
        List<Account> accounts = accountRepository.findAllByTenantId(tenantId);
        List<JournalEntry> entries = journalEntryRepository.findByTenantIdAndPeriod(tenantId, year, month);

        List<TrialBalanceEntry> trialEntries = new ArrayList<>();
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (Account account : accounts) {
            BigDecimal debitBalance = BigDecimal.ZERO;
            BigDecimal creditBalance = BigDecimal.ZERO;

            for (JournalEntry entry : entries) {
                for (JournalEntryLine line : entry.getLines()) {
                    if (line.getAccountId().equals(account.getId())) {
                        if (line.getSide() == DebitCredit.DEBIT) {
                            debitBalance = debitBalance.add(line.getAmount().getAmount());
                        } else {
                            creditBalance = creditBalance.add(line.getAmount().getAmount());
                        }
                    }
                }
            }

            trialEntries.add(TrialBalanceEntry.builder()
                .accountCode(account.getCode())
                .accountName(account.getName())
                .accountType(account.getType().name())
                .debitBalance(debitBalance)
                .creditBalance(creditBalance)
                .build());

            totalDebits = totalDebits.add(debitBalance);
            totalCredits = totalCredits.add(creditBalance);
        }

        return TrialBalanceResponse.builder()
            .tenantId(tenantId)
            .period(period.toLabel())
            .entries(trialEntries)
            .totalDebits(totalDebits.setScale(4, RoundingMode.HALF_UP))
            .totalCredits(totalCredits.setScale(4, RoundingMode.HALF_UP))
            .build();
    }

    public ProfitLossResponse generateProfitAndLoss(String tenantId, int startYear, int startMonth,
                                                     int endYear, int endMonth) {
        List<ProfitLossEntry> revenues = new ArrayList<>();
        List<ProfitLossEntry> expenses = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        int year = startYear;
        int month = startMonth;
        while (year < endYear || (year == endYear && month <= endMonth)) {
            List<JournalEntry> entries = journalEntryRepository.findByTenantIdAndPeriod(tenantId, year, month);
            for (JournalEntry entry : entries) {
                for (JournalEntryLine line : entry.getLines()) {
                    Account account = accountRepository.findById(line.getAccountId()).orElse(null);
                    if (account == null) continue;

                    BigDecimal lineAmount = line.getAmount().getAmount();
                    if (account.getType() == AccountType.REVENUE) {
                        revenues.add(ProfitLossEntry.builder()
                            .accountCode(account.getCode())
                            .accountName(account.getName())
                            .amount(lineAmount)
                            .build());
                        totalRevenue = totalRevenue.add(lineAmount);
                    } else if (account.getType() == AccountType.EXPENSE) {
                        expenses.add(ProfitLossEntry.builder()
                            .accountCode(account.getCode())
                            .accountName(account.getName())
                            .amount(lineAmount)
                            .build());
                        totalExpenses = totalExpenses.add(lineAmount);
                    }
                }
            }

            month++;
            if (month > 12) { month = 1; year++; }
        }

        return ProfitLossResponse.builder()
            .tenantId(tenantId)
            .periodStart(new FiscalPeriod(startYear, startMonth).toLabel())
            .periodEnd(new FiscalPeriod(endYear, endMonth).toLabel())
            .revenues(revenues)
            .expenses(expenses)
            .totalRevenue(totalRevenue.setScale(4, RoundingMode.HALF_UP))
            .totalExpenses(totalExpenses.setScale(4, RoundingMode.HALF_UP))
            .netProfitLoss(totalRevenue.subtract(totalExpenses).setScale(4, RoundingMode.HALF_UP))
            .build();
    }

    public BalanceSheetResponse generateBalanceSheet(String tenantId, Instant asOfDate) {
        List<Account> accounts = accountRepository.findAllByTenantId(tenantId);
        List<BalanceSheetSection> assets = new ArrayList<>();
        List<BalanceSheetSection> liabilities = new ArrayList<>();
        List<BalanceSheetSection> equity = new ArrayList<>();

        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal totalLiabilities = BigDecimal.ZERO;
        BigDecimal totalEquity = BigDecimal.ZERO;

        for (Account account : accounts) {
            BigDecimal balance = account.getCurrentBalance().getAmount();
            BalanceSheetSection section = BalanceSheetSection.builder()
                .accountCode(account.getCode())
                .accountName(account.getName())
                .amount(balance)
                .build();

            switch (account.getType()) {
                case ASSET -> { assets.add(section); totalAssets = totalAssets.add(balance); }
                case LIABILITY -> { liabilities.add(section); totalLiabilities = totalLiabilities.add(balance); }
                case EQUITY -> { equity.add(section); totalEquity = totalEquity.add(balance); }
            }
        }

        return BalanceSheetResponse.builder()
            .tenantId(tenantId)
            .asOfDate(asOfDate.toString())
            .assets(assets)
            .liabilities(liabilities)
            .equity(equity)
            .totalAssets(totalAssets.setScale(4, RoundingMode.HALF_UP))
            .totalLiabilities(totalLiabilities.setScale(4, RoundingMode.HALF_UP))
            .totalEquity(totalEquity.setScale(4, RoundingMode.HALF_UP))
            .build();
    }

    @Cacheable(value = "accounts", key = "'dashboard-' + #tenantId")
    public FinanceDashboardResponse getDashboardSummary(String tenantId) {
        long overdueCount = invoiceRepository.findOverdueByTenantId(tenantId).size();

        return FinanceDashboardResponse.builder()
            .totalOutstandingInvoices(BigDecimal.ZERO)
            .totalOverdueAmount(BigDecimal.ZERO)
            .overdueCount(overdueCount)
            .pendingApprovalCount(invoiceRepository.countByTenantIdAndStatus(tenantId, com.orvion.finance.domain.model.enums.InvoiceStatus.PENDING_APPROVAL))
            .cashBalance(BigDecimal.ZERO)
            .accountsReceivable(BigDecimal.ZERO)
            .accountsPayable(BigDecimal.ZERO)
            .monthlyRevenue(BigDecimal.ZERO)
            .monthlyExpenses(BigDecimal.ZERO)
            .currency("IDR")
            .build();
    }
}
