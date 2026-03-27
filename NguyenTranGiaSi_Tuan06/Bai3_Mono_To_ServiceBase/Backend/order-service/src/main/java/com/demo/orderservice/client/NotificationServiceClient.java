package com.demo.orderservice.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NotificationServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.notification-url}")
    private String notificationServiceUrl;

    public void send(Long userId, String message, Long orderId) {
        try {
            String url = notificationServiceUrl + "/api/notifications";
            SendRequest body = new SendRequest(userId, message, orderId);
            restTemplate.postForObject(url, body, Void.class);
        } catch (Exception ignored) {
            // best-effort: không làm fail order nếu notification lỗi
        }
    }

    @Data
    private static class SendRequest {
        private final Long userId;
        private final String message;
        private final Long orderId;
    }
}
