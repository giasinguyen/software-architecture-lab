package com.iuh.fit.resilienceprovider.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provider Controller - Giả lập service trả dữ liệu với các tình huống lỗi
 */
@RestController
@RequestMapping("/api/provider")
public class ProviderController {

    private static final Logger log = LoggerFactory.getLogger(ProviderController.class);
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Random random = new Random();

    // Luôn thành công - dùng cho Rate Limiter
    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getData() {
        int count = counter.incrementAndGet();
        log.info("[Provider] Request #{} - OK", count);
        
        Map<String, Object> data = new HashMap<>();
        data.put("product", "Laptop");
        data.put("price", 25000000);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("requestNumber", count);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    // 50% random lỗi - dùng cho Retry
    @GetMapping("/unstable")
    public ResponseEntity<Map<String, Object>> getUnstable() {
        int count = counter.incrementAndGet();
        if (random.nextBoolean()) {
            log.error("[Provider] Request #{} - FAIL", count);
            throw new RuntimeException("Lỗi giả lập 500!");
        }
        log.info("[Provider] Request #{} - OK", count);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("requestNumber", count);
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    // Delay n giây - dùng cho Bulkhead
    @GetMapping("/slow")
    public ResponseEntity<Map<String, Object>> getSlow(@RequestParam(defaultValue = "3") int delaySeconds) {
        int count = counter.incrementAndGet();
        log.info("[Provider] Request #{} - Delay {}s...", count, delaySeconds);
        try {
            Thread.sleep(delaySeconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("[Provider] Request #{} - Done", count);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("requestNumber", count);
        response.put("delaySeconds", delaySeconds);
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    // Luôn lỗi - dùng cho Circuit Breaker
    @GetMapping("/always-fail")
    public ResponseEntity<Map<String, Object>> alwaysFail() {
        int count = counter.incrementAndGet();
        log.error("[Provider] Request #{} - ALWAYS FAIL", count);
        throw new RuntimeException("Endpoint luôn lỗi - test Circuit Breaker");
    }

    // Xem status
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "resilience-provider");
        response.put("totalRequests", counter.get());
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    // Reset counter
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> reset() {
        int old = counter.getAndSet(0);
        log.info("[Provider] Reset counter: {} -> 0", old);
        
        Map<String, Object> response = new HashMap<>();
        response.put("oldCount", old);
        response.put("newCount", 0);
        
        return ResponseEntity.ok(response);
    }
}
