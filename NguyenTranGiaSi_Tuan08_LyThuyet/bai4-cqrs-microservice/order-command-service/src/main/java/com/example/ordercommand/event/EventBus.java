package com.example.ordercommand.event;

import com.example.ordercommand.dto.OrderEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class EventBus {

    @Value("${query.service.url}")
    private String queryServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void publish(OrderEventDto event) {
        try {
            log.info("Publishing event to query service: {}", event.getType());
            restTemplate.postForEntity(queryServiceUrl + "/events", event, Void.class);
            log.info("Event published successfully");
        } catch (Exception e) {
            log.error("Failed to publish event to query service: {}", e.getMessage());
        }
    }
}
