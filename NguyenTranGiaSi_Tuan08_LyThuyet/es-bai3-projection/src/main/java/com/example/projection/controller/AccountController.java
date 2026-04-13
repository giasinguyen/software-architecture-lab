package com.example.projection.controller;

import com.example.projection.read.AccountSummary;
import com.example.projection.read.ProjectionService;
import com.example.projection.write.CommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final CommandService commandService;
    private final ProjectionService projectionService;

    // ===== WRITE =====

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(commandService.createAccount(body.getOrDefault("initialDeposit", 0.0)));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountSummary> deposit(@PathVariable String id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(commandService.deposit(id, body.get("amount")));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountSummary> withdraw(@PathVariable String id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(commandService.withdraw(id, body.get("amount")));
    }

    // ===== READ (Projection) =====

    @GetMapping("/{id}/summary")
    public ResponseEntity<AccountSummary> getSummary(@PathVariable String id) {
        return ResponseEntity.ok(projectionService.getSummary(id));
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<AccountSummary>> getAll() {
        return ResponseEntity.ok(projectionService.getAllSummaries());
    }

    @PostMapping("/{id}/rebuild")
    public ResponseEntity<AccountSummary> rebuild(@PathVariable String id) {
        return ResponseEntity.ok(projectionService.buildSummary(id));
    }
}
