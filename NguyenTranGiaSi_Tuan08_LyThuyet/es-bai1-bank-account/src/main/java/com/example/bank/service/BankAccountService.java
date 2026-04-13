package com.example.bank.service;

import com.example.bank.aggregate.BankAccountAggregate;
import com.example.bank.event.AccountEvent;
import com.example.bank.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final EventRepository eventRepository;

    public Map<String, Object> createAccount(double initialDeposit) {
        String accountId = UUID.randomUUID().toString().substring(0, 8);
        AccountEvent event = AccountEvent.builder()
                .accountId(accountId)
                .eventType("ACCOUNT_CREATED")
                .amount(initialDeposit)
                .createdAt(LocalDateTime.now())
                .build();
        eventRepository.save(event);
        return Map.of("accountId", accountId, "balance", initialDeposit);
    }

    public Map<String, Object> deposit(String accountId, double amount) {
        AccountEvent event = AccountEvent.builder()
                .accountId(accountId)
                .eventType("MONEY_DEPOSITED")
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();
        eventRepository.save(event);
        BankAccountAggregate aggregate = getAggregate(accountId);
        return Map.of("accountId", accountId, "balance", aggregate.getBalance());
    }

    public Map<String, Object> withdraw(String accountId, double amount) {
        BankAccountAggregate current = getAggregate(accountId);
        if (current.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance. Current: " + current.getBalance());
        }
        AccountEvent event = AccountEvent.builder()
                .accountId(accountId)
                .eventType("MONEY_WITHDRAWN")
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();
        eventRepository.save(event);
        return Map.of("accountId", accountId, "balance", current.getBalance() - amount);
    }

    public Map<String, Object> getBalance(String accountId) {
        BankAccountAggregate aggregate = getAggregate(accountId);
        return Map.of("accountId", accountId, "balance", aggregate.getBalance());
    }

    public List<AccountEvent> getEvents(String accountId) {
        return eventRepository.findByAccountIdOrderByIdAsc(accountId);
    }

    private BankAccountAggregate getAggregate(String accountId) {
        List<AccountEvent> events = eventRepository.findByAccountIdOrderByIdAsc(accountId);
        if (events.isEmpty()) throw new RuntimeException("Account not found: " + accountId);
        return BankAccountAggregate.replay(events);
    }
}
