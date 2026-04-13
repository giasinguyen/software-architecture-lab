package com.example.snapshot.controller;

import com.example.snapshot.model.AccountSnapshot;
import com.example.snapshot.service.SnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class SnapshotController {

    private final SnapshotService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(service.createAccount(body.getOrDefault("initialDeposit", 0.0)));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable String id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(service.deposit(id, body.get("amount")));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable String id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(service.withdraw(id, body.get("amount")));
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<?> getState(@PathVariable String id) {
        return ResponseEntity.ok(service.getState(id));
    }

    @GetMapping("/{id}/snapshots")
    public ResponseEntity<List<AccountSnapshot>> getSnapshots(@PathVariable String id) {
        return ResponseEntity.ok(service.getSnapshots(id));
    }
}
