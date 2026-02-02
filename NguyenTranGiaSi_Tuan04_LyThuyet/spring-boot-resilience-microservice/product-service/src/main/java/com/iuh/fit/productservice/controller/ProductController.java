package com.iuh.fit.productservice.controller;

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
 * Product Controller - API cung cấp thông tin sản phẩm
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Random random = new Random();

    // Endpoint luôn thành công
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getProducts() {
        int count = counter.incrementAndGet();
        log.info("[Product Service] Request #{} - OK", count);
        
        Map<String, Object> product = new HashMap<>();
        product.put("id", 1);
        product.put("name", "Laptop Dell XPS 15");
        product.put("price", 25000000);
        product.put("stock", 50);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("requestNumber", count);
        response.put("product", product);
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    // 50% random lỗi - dùng để test Retry
    @GetMapping("/unstable")
    public ResponseEntity<Map<String, Object>> getUnstableProduct() {
        int count = counter.incrementAndGet();
        if (random.nextBoolean()) {
            log.error("[Product Service] Request #{} - RANDOM FAIL", count);
            throw new RuntimeException("Product database connection timeout!");
        }
        log.info("[Product Service] Request #{} - OK after potential retry", count);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("productId", 123);
        response.put("message", "Retrieved after potential retry");
        response.put("requestNumber", count);
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    // Endpoint delay - test Bulkhead
    @GetMapping("/slow")
    public ResponseEntity<Map<String, Object>> getSlowProduct(@RequestParam(defaultValue = "3") int delaySeconds) {
        int count = counter.incrementAndGet();
        log.info("[Product Service] Request #{} - Delay {}s...", count, delaySeconds);
        try {
            Thread.sleep(delaySeconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Slow product query completed");
        response.put("delaySeconds", delaySeconds);
        response.put("requestNumber", count);
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    // Endpoint luôn lỗi - test Circuit Breaker
    @GetMapping("/error")
    public ResponseEntity<Map<String, Object>> getErrorProduct() {
        int count = counter.incrementAndGet();
        log.error("[Product Service] Request #{} - ALWAYS FAIL", count);
        throw new RuntimeException("Product service is down!");
    }

    // Xem trạng thái
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Product Service");
        response.put("status", "RUNNING");
        response.put("totalRequests", counter.get());
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    // Reset counter
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> reset() {
        int oldValue = counter.getAndSet(0);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Counter reset");
        response.put("oldValue", oldValue);
        response.put("newValue", 0);
        return ResponseEntity.ok(response);
    }
}

