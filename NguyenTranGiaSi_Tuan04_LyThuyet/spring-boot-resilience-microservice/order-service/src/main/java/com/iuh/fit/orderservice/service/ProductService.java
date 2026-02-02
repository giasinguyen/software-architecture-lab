package com.iuh.fit.orderservice.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * ProductService - Gọi Product Service với các cơ chế Resilience4j
 * FALLBACK sẽ throw exception để báo lỗi thực tế, KHÔNG dùng dữ liệu giả
 */
@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final RestTemplate restTemplate;
    
    @Value("${product.service.url}")
    private String productServiceUrl;

    public ProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 1. RETRY: Tự động thử lại khi lỗi (max 3 lần, cách 2s)
    @Retry(name = "productRetry", fallbackMethod = "handleError")
    public Map<String, Object> getProductWithRetry() {
        log.info("[RETRY] Calling Product Service /unstable...");
        return restTemplate.getForObject(productServiceUrl + "/api/products/unstable", Map.class);
    }

    // 2. CIRCUIT BREAKER: Ngắt mạch khi lỗi >50% trong 5 request
    @CircuitBreaker(name = "productCircuitBreaker", fallbackMethod = "handleCircuitBreakerError")
    public Map<String, Object> getProductWithCircuitBreaker() {
        log.info("[CIRCUIT BREAKER] Calling Product Service /error...");
        return restTemplate.getForObject(productServiceUrl + "/api/products/error", Map.class);
    }

    // 3. RATE LIMITER: Giới hạn 5 request/10 giây
    @RateLimiter(name = "productRateLimiter", fallbackMethod = "handleRateLimitError")
    public Map<String, Object> getProductWithRateLimiter() {
        log.info("[RATE LIMITER] Calling Product Service /list...");
        return restTemplate.getForObject(productServiceUrl + "/api/products/list", Map.class);
    }

    // 4. BULKHEAD: Giới hạn 3 request đồng thời
    @Bulkhead(name = "productBulkhead", fallbackMethod = "handleBulkheadError")
    public Map<String, Object> getProductWithBulkhead() {
        log.info("[BULKHEAD] Calling Product Service /slow...");
        return restTemplate.getForObject(productServiceUrl + "/api/products/slow?delaySeconds=3", Map.class);
    }

    // 5. KẾT HỢP: Rate Limiter + Circuit Breaker + Retry
    @RateLimiter(name = "productRateLimiter")
    @CircuitBreaker(name = "productCircuitBreaker", fallbackMethod = "handleError")
    @Retry(name = "productRetry")
    public Map<String, Object> getProductWithCombined() {
        log.info("[COMBINED] Calling Product Service /unstable...");
        return restTemplate.getForObject(productServiceUrl + "/api/products/unstable", Map.class);
    }

    // FALLBACK: Báo lỗi thực tế, KHÔNG trả fake data
    private Map<String, Object> handleError(Exception ex) {
        log.error("[FALLBACK] Product Service ERROR: {}", ex.getMessage());
        throw new RuntimeException("Product Service unavailable after retry: " + ex.getMessage());
    }

    private Map<String, Object> handleCircuitBreakerError(Exception ex) {
        log.error("[FALLBACK - CIRCUIT BREAKER] Circuit is OPEN: {}", ex.getMessage());
        throw new RuntimeException("Product Service circuit breaker is OPEN - service is down!");
    }

    private Map<String, Object> handleRateLimitError(Exception ex) {
        log.error("[FALLBACK - RATE LIMIT] Too many requests: {}", ex.getMessage());
        throw new RuntimeException("Rate limit exceeded - too many requests to Product Service!");
    }

    private Map<String, Object> handleBulkheadError(Exception ex) {
        log.error("[FALLBACK - BULKHEAD] Bulkhead full: {}", ex.getMessage());
        throw new RuntimeException("Product Service overloaded - max concurrent calls reached!");
    }
}
