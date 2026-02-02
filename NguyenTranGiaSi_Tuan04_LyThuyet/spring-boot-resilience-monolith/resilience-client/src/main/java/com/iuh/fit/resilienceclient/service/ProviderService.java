package com.iuh.fit.resilienceclient.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service gọi Provider với các cơ chế Resilience4j
 */
@Service
public class ProviderService {

    private static final Logger log = LoggerFactory.getLogger(ProviderService.class);
    private final RestTemplate restTemplate;

    public ProviderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    private static final String PROVIDER_URL = "http://localhost:8081/api/provider";

    // 1. RETRY: Tự động thử lại khi lỗi (max 3 lần, cách 2s)
    @Retry(name = "providerRetry", fallbackMethod = "fallback")
    public Map<String, Object> callWithRetry() {
        log.info("[RETRY] Gọi Provider /unstable...");
        return restTemplate.getForObject(PROVIDER_URL + "/unstable", Map.class);
    }

    // 2. CIRCUIT BREAKER: Ngắt mạch khi lỗi >50% trong 5 request
    @CircuitBreaker(name = "providerCircuitBreaker", fallbackMethod = "fallback")
    public Map<String, Object> callWithCircuitBreaker() {
        log.info("[CIRCUIT BREAKER] Gọi Provider /always-fail...");
        return restTemplate.getForObject(PROVIDER_URL + "/always-fail", Map.class);
    }

    // 3. RATE LIMITER: Giới hạn 5 request/10 giây
    @RateLimiter(name = "providerRateLimiter", fallbackMethod = "fallback")
    public Map<String, Object> callWithRateLimiter() {
        log.info("[RATE LIMITER] Gọi Provider /data...");
        return restTemplate.getForObject(PROVIDER_URL + "/data", Map.class);
    }

    // 4. BULKHEAD: Giới hạn 3 request đồng thời
    @Bulkhead(name = "providerBulkhead", fallbackMethod = "fallback")
    public Map<String, Object> callWithBulkhead() {
        log.info("[BULKHEAD] Gọi Provider /slow...");
        return restTemplate.getForObject(PROVIDER_URL + "/slow?delaySeconds=3", Map.class);
    }

    // 5. KẾT HỢP: Rate Limiter + Circuit Breaker + Retry
    @RateLimiter(name = "providerRateLimiter")
    @CircuitBreaker(name = "providerCircuitBreaker", fallbackMethod = "fallback")
    @Retry(name = "providerRetry")
    public Map<String, Object> callWithCombinedResilience() {
        log.info("[COMBINED] Gọi Provider /unstable...");
        return restTemplate.getForObject(PROVIDER_URL + "/unstable", Map.class);
    }

    // Fallback chung cho tất cả pattern
    public Map<String, Object> fallback(Exception ex) {
        log.warn("[FALLBACK] Lỗi: {}", ex.getMessage());
        Map<String, Object> result = new HashMap<>();
        result.put("status", "FALLBACK");
        result.put("message", "Dữ liệu dự phòng");
        result.put("error", ex.getMessage());
        result.put("timestamp", LocalDateTime.now().toString());
        return result;
    }
}
