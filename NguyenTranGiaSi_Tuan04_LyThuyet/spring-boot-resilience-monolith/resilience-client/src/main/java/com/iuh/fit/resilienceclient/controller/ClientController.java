package com.iuh.fit.resilienceclient.controller;

import com.iuh.fit.resilienceclient.service.ProviderService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Client Controller - Nhận request và gọi Provider với Resilience4j
 */
@RestController
@RequestMapping("/api/client")
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
    private final ProviderService providerService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public ClientController(ProviderService providerService, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.providerService = providerService;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    // Test RETRY: Gọi 1 lần, xem log retry
    @GetMapping("/retry")
    public ResponseEntity<Map<String, Object>> testRetry() {
        log.info("=== TEST RETRY ===");
        Map<String, Object> response = new HashMap<>();
        response.put("pattern", "RETRY");
        response.put("result", providerService.callWithRetry());
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // Test CIRCUIT BREAKER: Gọi 5+ lần để thấy circuit OPEN
    @GetMapping("/circuit")
    public ResponseEntity<Map<String, Object>> testCircuitBreaker() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("providerCircuitBreaker");
        String stateBefore = cb.getState().name();
        
        log.info("=== TEST CIRCUIT BREAKER === State: {}", stateBefore);
        Map<String, Object> result = providerService.callWithCircuitBreaker();
        
        Map<String, Object> response = new HashMap<>();
        response.put("pattern", "CIRCUIT_BREAKER");
        response.put("stateBefore", stateBefore);
        response.put("stateAfter", cb.getState().name());
        response.put("result", result);
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // Test RATE LIMITER: Gọi >5 lần trong 10s
    @GetMapping("/rate-limit")
    public ResponseEntity<Map<String, Object>> testRateLimiter() {
        log.info("=== TEST RATE LIMITER ===");
        Map<String, Object> response = new HashMap<>();
        response.put("pattern", "RATE_LIMITER");
        response.put("result", providerService.callWithRateLimiter());
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // Test BULKHEAD: Gọi >3 request đồng thời
    @GetMapping("/bulkhead")
    public ResponseEntity<Map<String, Object>> testBulkhead() {
        log.info("=== TEST BULKHEAD ===");
        Map<String, Object> response = new HashMap<>();
        response.put("pattern", "BULKHEAD");
        response.put("result", providerService.callWithBulkhead());
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // Test KẾT HỢP nhiều pattern
    @GetMapping("/combined")
    public ResponseEntity<Map<String, Object>> testCombined() {
        log.info("=== TEST COMBINED ===");
        Map<String, Object> response = new HashMap<>();
        response.put("pattern", "COMBINED");
        response.put("result", providerService.callWithCombinedResilience());
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // Xem trạng thái Circuit Breaker
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("providerCircuitBreaker");
        Map<String, Object> response = new HashMap<>();
        response.put("circuitState", cb.getState().name());
        response.put("failureRate", cb.getMetrics().getFailureRate());
        response.put("successCalls", cb.getMetrics().getNumberOfSuccessfulCalls());
        response.put("failedCalls", cb.getMetrics().getNumberOfFailedCalls());
        return ResponseEntity.ok(response);
    }

    // Reset Circuit Breaker
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetCircuit() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("providerCircuitBreaker");
        String oldState = cb.getState().name();
        cb.reset();
        log.info("Reset Circuit: {} -> CLOSED", oldState);
        Map<String, Object> response = new HashMap<>();
        response.put("oldState", oldState);
        response.put("newState", "CLOSED");
        return ResponseEntity.ok(response);
    }
}
