package com.example.snapshot.service;

import com.example.snapshot.model.AccountEvent;
import com.example.snapshot.model.AccountSnapshot;
import com.example.snapshot.repository.EventRepository;
import com.example.snapshot.repository.SnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnapshotService {

    private final EventRepository eventRepository;
    private final SnapshotRepository snapshotRepository;

    @Value("${snapshot.interval:3}")
    private int snapshotInterval;

    @Transactional
    public Map<String, Object> createAccount(double initialDeposit) {
        String accountId = UUID.randomUUID().toString().substring(0, 8);
        AccountEvent event = eventRepository.save(AccountEvent.builder()
                .accountId(accountId).eventType("ACCOUNT_CREATED")
                .amount(initialDeposit).createdAt(LocalDateTime.now()).build());
        checkAndCreateSnapshot(accountId);
        return Map.of("accountId", accountId, "balance", initialDeposit);
    }

    @Transactional
    public Map<String, Object> deposit(String accountId, double amount) {
        eventRepository.save(AccountEvent.builder()
                .accountId(accountId).eventType("MONEY_DEPOSITED")
                .amount(amount).createdAt(LocalDateTime.now()).build());
        checkAndCreateSnapshot(accountId);
        double balance = getBalance(accountId);
        return Map.of("accountId", accountId, "balance", balance);
    }

    @Transactional
    public Map<String, Object> withdraw(String accountId, double amount) {
        double currentBalance = getBalance(accountId);
        if (currentBalance < amount) throw new RuntimeException("Insufficient balance");
        eventRepository.save(AccountEvent.builder()
                .accountId(accountId).eventType("MONEY_WITHDRAWN")
                .amount(amount).createdAt(LocalDateTime.now()).build());
        checkAndCreateSnapshot(accountId);
        return Map.of("accountId", accountId, "balance", currentBalance - amount);
    }

    /** Get balance using snapshot + newer events (optimized replay) */
    public double getBalance(String accountId) {
        Optional<AccountSnapshot> snapshot = snapshotRepository.findTopByAccountIdOrderByIdDesc(accountId);

        double balance;
        List<AccountEvent> events;

        if (snapshot.isPresent()) {
            balance = snapshot.get().getBalance();
            events = eventRepository.findByAccountIdAndIdGreaterThanOrderByIdAsc(
                    accountId, snapshot.get().getLastEventId());
            log.info("Replaying from snapshot (balance={}) + {} new events", balance, events.size());
        } else {
            balance = 0;
            events = eventRepository.findByAccountIdOrderByIdAsc(accountId);
            log.info("No snapshot found. Replaying all {} events", events.size());
        }

        for (AccountEvent e : events) {
            balance = switch (e.getEventType()) {
                case "ACCOUNT_CREATED", "MONEY_DEPOSITED" -> balance + e.getAmount();
                case "MONEY_WITHDRAWN" -> balance - e.getAmount();
                default -> balance;
            };
        }
        return balance;
    }

    public Map<String, Object> getState(String accountId) {
        double balance = getBalance(accountId);
        Optional<AccountSnapshot> snapshot = snapshotRepository.findTopByAccountIdOrderByIdDesc(accountId);
        long totalEvents = eventRepository.countByAccountId(accountId);

        Map<String, Object> state = new LinkedHashMap<>();
        state.put("accountId", accountId);
        state.put("balance", balance);
        state.put("totalEvents", totalEvents);
        state.put("snapshotInterval", snapshotInterval);
        snapshot.ifPresent(s -> {
            state.put("lastSnapshotId", s.getId());
            state.put("snapshotBalance", s.getBalance());
            state.put("snapshotAtEventId", s.getLastEventId());
        });
        return state;
    }

    public List<AccountSnapshot> getSnapshots(String accountId) {
        return snapshotRepository.findAll().stream()
                .filter(s -> s.getAccountId().equals(accountId)).toList();
    }

    private void checkAndCreateSnapshot(String accountId) {
        long totalEvents = eventRepository.countByAccountId(accountId);
        Optional<AccountSnapshot> lastSnapshot = snapshotRepository.findTopByAccountIdOrderByIdDesc(accountId);

        long eventsSinceSnapshot;
        if (lastSnapshot.isPresent()) {
            eventsSinceSnapshot = eventRepository.findByAccountIdAndIdGreaterThanOrderByIdAsc(
                    accountId, lastSnapshot.get().getLastEventId()).size();
        } else {
            eventsSinceSnapshot = totalEvents;
        }

        if (eventsSinceSnapshot >= snapshotInterval) {
            double balance = getBalance(accountId);
            List<AccountEvent> allEvents = eventRepository.findByAccountIdOrderByIdAsc(accountId);
            Long lastEventId = allEvents.get(allEvents.size() - 1).getId();

            snapshotRepository.save(AccountSnapshot.builder()
                    .accountId(accountId).balance(balance)
                    .lastEventId(lastEventId).createdAt(LocalDateTime.now()).build());
            log.info("Snapshot created for account {} at event {} with balance {}", accountId, lastEventId, balance);
        }
    }
}
