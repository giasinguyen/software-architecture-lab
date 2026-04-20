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
 * Topic Subscriber – Created events.
 * Pattern: "*.created.*" → nhận mọi event có từ "created" ở vị trí thứ 2.
 * Ví dụ: orders.created.vn, payments.created.sg, users.created.us
 */
@Component
public class CreatedTopicSubscriber {

    private static final Logger log = LoggerFactory.getLogger(CreatedTopicSubscriber.class);
    private final MessageStore store;

    public CreatedTopicSubscriber(MessageStore store) {
        this.store = store;
    }

    @RabbitListener(queues = RabbitMQConfig.TOPIC_QUEUE_CREATED)
    public void onCreatedEvent(Map<String, Object> event) {
        log.info("✨ ══ [Topic: *.created.*] ═════════════════════════");
        log.info("  routingKey : {}", event.get("routingKey"));
        log.info("  message    : {}", event.get("message"));
        log.info("  timestamp  : {}", event.get("timestamp"));
        log.info("══════════════════════════════════════════════════");

        store.add(Map.of(
                "consumer", "CreatedTopicSubscriber",
                "queue", RabbitMQConfig.TOPIC_QUEUE_CREATED,
                "type", "topic",
                "receivedAt", LocalDateTime.now().toString(),
                "data", event
        ));
    }
}
