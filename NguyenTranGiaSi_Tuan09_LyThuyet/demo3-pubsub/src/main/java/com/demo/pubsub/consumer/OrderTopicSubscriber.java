package com.demo.pubsub.consumer;

import com.demo.pubsub.config.RabbitMQConfig;
import com.demo.pubsub.service.MessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Topic Subscriber – Orders.
 * Pattern: "orders.#" → nhận mọi event bắt đầu bằng "orders."
 * Ví dụ: orders.created.vn, orders.shipped.sg, orders.cancelled.vn
 */
@Component
public class OrderTopicSubscriber {

    private static final Logger log = LoggerFactory.getLogger(OrderTopicSubscriber.class);
    private final MessageStore store;

    public OrderTopicSubscriber(MessageStore store) {
        this.store = store;
    }

    @RabbitListener(queues = RabbitMQConfig.TOPIC_QUEUE_ORDERS)
    public void onOrderEvent(Map<String, Object> event) {
        log.info("🛒 ══ [Topic: orders.#] ═══════════════════════════");
        log.info("  routingKey : {}", event.get("routingKey"));
        log.info("  message    : {}", event.get("message"));
        log.info("  timestamp  : {}", event.get("timestamp"));
        log.info("══════════════════════════════════════════════════");
        store.add(Map.of(
                "consumer", "OrderTopicSubscriber",
                "queue", RabbitMQConfig.TOPIC_QUEUE_ORDERS,
                "type", "topic",
                "receivedAt", LocalDateTime.now().toString(),
                "data", event
        ));    }
}
