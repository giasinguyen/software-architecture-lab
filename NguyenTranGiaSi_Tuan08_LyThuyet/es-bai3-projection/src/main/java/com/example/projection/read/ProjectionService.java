package com.example.projection.read;

import com.example.projection.write.AccountEvent;
import com.example.projection.write.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectionService {

    private final EventRepository eventRepository;
    private final AccountSummaryRepository summaryRepository;

    /** Rebuild projection from events */
    public AccountSummary buildSummary(String accountId) {
        List<AccountEvent> events = eventRepository.findByAccountIdOrderByIdAsc(accountId);
        if (events.isEmpty()) throw new RuntimeException("Account not found: " + accountId);

        double balance = 0, totalDeposits = 0, totalWithdrawals = 0;
        int txCount = 0;

        for (AccountEvent e : events) {
            switch (e.getEventType()) {
                case "ACCOUNT_CREATED" -> { balance += e.getAmount(); totalDeposits += e.getAmount(); }
                case "MONEY_DEPOSITED" -> { balance += e.getAmount(); totalDeposits += e.getAmount(); txCount++; }
                case "MONEY_WITHDRAWN" -> { balance -= e.getAmount(); totalWithdrawals += e.getAmount(); txCount++; }
            }
        }

        AccountSummary summary = AccountSummary.builder()
                .accountId(accountId)
                .balance(balance)
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .transactionCount(txCount)
                .build();
        return summaryRepository.save(summary);
    }

    public AccountSummary getSummary(String accountId) {
        return summaryRepository.findById(accountId)
                .orElseGet(() -> buildSummary(accountId));
    }

    public List<AccountSummary> getAllSummaries() {
        return summaryRepository.findAll();
    }
}
