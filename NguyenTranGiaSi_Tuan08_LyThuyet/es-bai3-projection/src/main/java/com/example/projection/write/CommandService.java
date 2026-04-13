package com.example.projection.write;

import com.example.projection.read.AccountSummary;
import com.example.projection.read.ProjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommandService {

    private final EventRepository eventRepository;
    private final ProjectionService projectionService;

    public Map<String, Object> createAccount(double initialDeposit) {
        String accountId = UUID.randomUUID().toString().substring(0, 8);
        eventRepository.save(AccountEvent.builder()
                .accountId(accountId).eventType("ACCOUNT_CREATED")
                .amount(initialDeposit).createdAt(LocalDateTime.now()).build());
        projectionService.buildSummary(accountId);
        return Map.of("accountId", accountId, "balance", initialDeposit);
    }

    public AccountSummary deposit(String accountId, double amount) {
        eventRepository.save(AccountEvent.builder()
                .accountId(accountId).eventType("MONEY_DEPOSITED")
                .amount(amount).createdAt(LocalDateTime.now()).build());
        return projectionService.buildSummary(accountId);
    }

    public AccountSummary withdraw(String accountId, double amount) {
        AccountSummary current = projectionService.getSummary(accountId);
        if (current.getBalance() < amount) throw new RuntimeException("Insufficient balance");
        eventRepository.save(AccountEvent.builder()
                .accountId(accountId).eventType("MONEY_WITHDRAWN")
                .amount(amount).createdAt(LocalDateTime.now()).build());
        return projectionService.buildSummary(accountId);
    }
}
