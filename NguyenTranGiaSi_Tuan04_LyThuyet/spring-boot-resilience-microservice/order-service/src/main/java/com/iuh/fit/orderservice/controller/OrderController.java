package com.iuh.fit.orderservice.controller;

import com.iuh.fit.orderservice.service.ProductService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Order Controller - API nhận order và gọi Product Service với Resilience4j
 * Fallback sẽ throw exception, controller này handle và trả HTTP error
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final ProductService productService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public OrderController(ProductService productService, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.productService = productService;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    // Test RETRY
    @GetMapping("/retry")
    public ResponseEntity<Map<String, Object>> testRetry() {
        log.info("=== TEST RETRY ===");
        try {
            Map<String, Object> result = productService.getProductWithRetry();
            return buildSuccessResponse("RETRY", result);
        } catch (Exception e) {
            return buildErrorResponse("RETRY", e.getMessage());
        }
    }

    // Test CIRCUIT BREAKER
    @GetMapping("/circuit-breaker")
    public ResponseEntity<Map<String, Object>> testCircuitBreaker() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("productCircuitBreaker");
        String stateBefore = cb.getState().name();
        log.info("=== TEST CIRCUIT BREAKER === State: {}", stateBefore);
        
        try {
            Map<String, Object> result = productService.getProductWithCircuitBreaker();
            return buildSuccessResponseWithCircuit("CIRCUIT_BREAKER", result, stateBefore, cb.getState().name());
        } catch (Exception e) {
            return buildErrorResponseWithCircuit("CIRCUIT_BREAKER", e.getMessage(), stateBefore, cb.getState().name());
        }
    }

    // Test RATE LIMITER
    @GetMapping("/rate-limiter")
    public ResponseEntity<Map<String, Object>> testRateLimiter() {
        log.info("=== TEST RATE LIMITER ===");
        try {
            Map<String, Object> result = productService.getProductWithRateLimiter();
            return buildSuccessResponse("RATE_LIMITER", result);
        } catch (Exception e) {
            return buildErrorResponse("RATE_LIMITER", e.getMessage());
        }
    }

    // Test BULKHEAD
    @GetMapping("/bulkhead")
    public ResponseEntity<Map<String, Object>> testBulkhead() {
        log.info("=== TEST BULKHEAD ===");
        try {
            Map<String, Object> result = productService.getProductWithBulkhead();
            return buildSuccessResponse("BULKHEAD", result);
        } catch (Exception e) {
            return buildErrorResponse("BULKHEAD", e.getMessage());
        }
    }

    // Test COMBINED
    @GetMapping("/combined")
    public ResponseEntity<Map<String, Object>> testCombined() {
        log.info("=== TEST COMBINED ===");
        try {
            Map<String, Object> result = productService.getProductWithCombined();
            return buildSuccessResponse("COMBINED", result);
        } catch (Exception e) {
            return buildErrorResponse("COMBINED", e.getMessage());
        }
    }

    // Xem status Circuit Breaker
    @GetMapping("/circuit-status")
    public ResponseEntity<Map<String, Object>> getCircuitStatus() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("productCircuitBreaker");
        Map<String, Object> response = new HashMap<>();
        response.put("circuitState", cb.getState().name());
        response.put("failureRate", cb.getMetrics().getFailureRate() + "%");
        response.put("successfulCalls", cb.getMetrics().getNumberOfSuccessfulCalls());
        response.put("failedCalls", cb.getMetrics().getNumberOfFailedCalls());
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // Reset Circuit Breaker
    @PostMapping("/circuit-reset")
    public ResponseEntity<Map<String, Object>> resetCircuit() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("productCircuitBreaker");
        String oldState = cb.getState().name();
        cb.reset();
        log.info("Circuit Breaker reset: {} -> CLOSED", oldState);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Circuit Breaker đã reset");
        response.put("oldState", oldState);
        response.put("newState", "CLOSED");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // Helper methods
    private ResponseEntity<Map<String, Object>> buildSuccessResponse(String pattern, Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("pattern", pattern);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String pattern, String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("pattern", pattern);
        response.put("error", errorMessage);
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    private ResponseEntity<Map<String, Object>> buildSuccessResponseWithCircuit(String pattern, Map<String, Object> data, 
                                                                                String stateBefore, String stateAfter) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("pattern", pattern);
        response.put("circuitStateBefore", stateBefore);
        response.put("circuitStateAfter", stateAfter);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponseWithCircuit(String pattern, String errorMessage, 
                                                                              String stateBefore, String stateAfter) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("pattern", pattern);
        response.put("circuitStateBefore", stateBefore);
        response.put("circuitStateAfter", stateAfter);
        response.put("error", errorMessage);
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
