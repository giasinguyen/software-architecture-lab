package com.example.bank.controller;

import com.example.bank.event.AccountEvent;
import com.example.bank.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService service;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(service.createAccount(body.getOrDefault("initialDeposit", 0.0)));
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<Map<String, Object>> deposit(@PathVariable String accountId, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(service.deposit(accountId, body.get("amount")));
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(@PathVariable String accountId, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(service.withdraw(accountId, body.get("amount")));
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable String accountId) {
        return ResponseEntity.ok(service.getBalance(accountId));
    }

    @GetMapping("/{accountId}/events")
    public ResponseEntity<List<AccountEvent>> getEvents(@PathVariable String accountId) {
        return ResponseEntity.ok(service.getEvents(accountId));
    }
}
