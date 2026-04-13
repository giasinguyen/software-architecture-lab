package com.example.timetravel.service;

import com.example.timetravel.event.AccountEvent;
import com.example.timetravel.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TimeTravelService {

    private final EventRepository eventRepository;

    public Map<String, Object> createAccount(String accountId, double initialDeposit) {
        eventRepository.save(AccountEvent.builder()
                .accountId(accountId).eventType("ACCOUNT_CREATED")
                .amount(initialDeposit).createdAt(LocalDateTime.now()).build());
        return Map.of("accountId", accountId, "balance", initialDeposit);
    }

    public Map<String, Object> deposit(String accountId, double amount) {
        eventRepository.save(AccountEvent.builder()
                .accountId(accountId).eventType("MONEY_DEPOSITED")
                .amount(amount).createdAt(LocalDateTime.now()).build());
        return getBalance(accountId);
    }

    public Map<String, Object> withdraw(String accountId, double amount) {
        double current = replayBalance(getEvents(accountId));
        if (current < amount) throw new RuntimeException("Insufficient balance");
        eventRepository.save(AccountEvent.builder()
                .accountId(accountId).eventType("MONEY_WITHDRAWN")
                .amount(amount).createdAt(LocalDateTime.now()).build());
        return getBalance(accountId);
    }

    public Map<String, Object> getBalance(String accountId) {
        List<AccountEvent> events = getEvents(accountId);
        return Map.of("accountId", accountId, "balance", replayBalance(events), "totalEvents", events.size());
    }

    /** Replay state up to a specific event index (0-based) */
    public Map<String, Object> getStateAt(String accountId, int index) {
        List<AccountEvent> events = getEvents(accountId);
        if (index < 0 || index >= events.size()) {
            throw new RuntimeException("Invalid index: " + index + ". Total events: " + events.size());
        }
        List<AccountEvent> subset = events.subList(0, index + 1);
        return Map.of("accountId", accountId, "balance", replayBalance(subset),
                "atEventIndex", index, "event", subset.get(index).getEventType());
    }

    /** Undo the last event (soft delete by marking) */
    public Map<String, Object> undoLastEvent(String accountId) {
        List<AccountEvent> events = getEvents(accountId);
        if (events.size() <= 1) throw new RuntimeException("Cannot undo: only creation event remains");
        AccountEvent last = events.get(events.size() - 1);
        eventRepository.delete(last);
        List<AccountEvent> remaining = events.subList(0, events.size() - 1);
        return Map.of("accountId", accountId, "balance", replayBalance(remaining),
                "undoneEvent", last.getEventType(), "undoneAmount", last.getAmount());
    }

    /** Get full event history with running balance at each step */
    public List<Map<String, Object>> getHistory(String accountId) {
        List<AccountEvent> events = getEvents(accountId);
        List<Map<String, Object>> history = new ArrayList<>();
        double balance = 0;
        for (int i = 0; i < events.size(); i++) {
            AccountEvent e = events.get(i);
            balance = applyEvent(balance, e);
            history.add(Map.of("index", i, "eventType", e.getEventType(),
                    "amount", e.getAmount(), "balanceAfter", balance, "createdAt", e.getCreatedAt().toString()));
        }
        return history;
    }

    private List<AccountEvent> getEvents(String accountId) {
        List<AccountEvent> events = eventRepository.findByAccountIdOrderByIdAsc(accountId);
        if (events.isEmpty()) throw new RuntimeException("Account not found: " + accountId);
        return events;
    }

    private double replayBalance(List<AccountEvent> events) {
        double balance = 0;
        for (AccountEvent e : events) balance = applyEvent(balance, e);
        return balance;
    }

    private double applyEvent(double balance, AccountEvent e) {
        return switch (e.getEventType()) {
            case "ACCOUNT_CREATED", "MONEY_DEPOSITED" -> balance + e.getAmount();
            case "MONEY_WITHDRAWN" -> balance - e.getAmount();
            default -> balance;
        };
    }
}
