package com.example.timetravel.controller;

import com.example.timetravel.service.TimeTravelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class TimeTravelController {

    private final TimeTravelService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.createAccount(
                (String) body.get("accountId"),
                ((Number) body.get("initialDeposit")).doubleValue()));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable String id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(service.deposit(id, body.get("amount")));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable String id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(service.withdraw(id, body.get("amount")));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<?> balance(@PathVariable String id) {
        return ResponseEntity.ok(service.getBalance(id));
    }

    @GetMapping("/{id}/state-at/{index}")
    public ResponseEntity<?> stateAt(@PathVariable String id, @PathVariable int index) {
        return ResponseEntity.ok(service.getStateAt(id, index));
    }

    @PostMapping("/{id}/undo")
    public ResponseEntity<?> undo(@PathVariable String id) {
        return ResponseEntity.ok(service.undoLastEvent(id));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> history(@PathVariable String id) {
        return ResponseEntity.ok(service.getHistory(id));
    }
}
