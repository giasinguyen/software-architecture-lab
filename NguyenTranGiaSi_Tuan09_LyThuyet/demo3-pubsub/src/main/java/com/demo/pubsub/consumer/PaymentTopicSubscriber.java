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
 * Topic Subscriber – Payments.
 * Pattern: "payments.#" → nhận mọi event bắt đầu bằng "payments."
 * Ví dụ: payments.success.vn, payments.failed.vn, payments.refunded.sg
 */
@Component
public class PaymentTopicSubscriber {

    private static final Logger log = LoggerFactory.getLogger(PaymentTopicSubscriber.class);
    private final MessageStore store;

    public PaymentTopicSubscriber(MessageStore store) {
        this.store = store;
    }

    @RabbitListener(queues = RabbitMQConfig.TOPIC_QUEUE_PAYMENTS)
    public void onPaymentEvent(Map<String, Object> event) {
        log.info("💰 ══ [Topic: payments.#] ═════════════════════════");
        log.info("  routingKey : {}", event.get("routingKey"));
        log.info("  message    : {}", event.get("message"));
        log.info("  timestamp  : {}", event.get("timestamp"));
        log.info("══════════════════════════════════════════════════");
        store.add(Map.of(
                "consumer", "PaymentTopicSubscriber",
                "queue", RabbitMQConfig.TOPIC_QUEUE_PAYMENTS,
                "type", "topic",
                "receivedAt", LocalDateTime.now().toString(),
                "data", event
        ));    }
}
