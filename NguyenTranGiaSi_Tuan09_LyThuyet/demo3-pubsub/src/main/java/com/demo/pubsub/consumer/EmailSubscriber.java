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
 * Fanout Subscriber – Email Service.
 * Nhận BẢN SAO của mọi message gửi tới fanout exchange (order_events).
 */
@Component
public class EmailSubscriber {

    private static final Logger log = LoggerFactory.getLogger(EmailSubscriber.class);
    private final MessageStore store;

    public EmailSubscriber(MessageStore store) {
        this.store = store;
    }

    @RabbitListener(queues = RabbitMQConfig.FANOUT_QUEUE_EMAIL)
    public void onOrderEvent(Map<String, Object> event) {
        log.info("📧 ══ [Email Subscriber] ═══════════════════════════");
        log.info("  event     : {}", event.get("event"));
        log.info("  orderId   : {}", event.get("orderId"));
        log.info("  total     : {}", event.get("total"));
        log.info("  → Sending confirmation email to customer...");
        log.info("══════════════════════════════════════════════════");

        store.add(Map.of(
                "consumer", "EmailSubscriber",
                "queue", RabbitMQConfig.FANOUT_QUEUE_EMAIL,
                "type", "fanout",
                "receivedAt", LocalDateTime.now().toString(),
                "data", event
        ));
    }
}
