package com.demo.broker.consumer;

import com.demo.broker.config.RabbitMQConfig;
import com.demo.broker.service.MessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Consumer lắng nghe Dead Letter Queue (DLQ).
 * Mọi message bị reject / hết TTL từ queue.info, queue.warning, queue.error
 * sẽ được chuyển tới đây qua Dead Letter Exchange (logs.dlx).
 */
@Component
public class DeadLetterConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterConsumer.class);
    private final MessageStore store;

    public DeadLetterConsumer(MessageStore store) {
        this.store = store;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_DLQ)
    public void handleDeadLetter(Map<String, Object> message) {
        log.warn("💀 ══ [DLQ Consumer] Dead Letter Received ════════");
        log.warn("  level     : {}", message.get("level"));
        log.warn("  service   : {}", message.get("service"));
        log.warn("  message   : {}", message.get("message"));
        log.warn("  timestamp : {}", message.get("timestamp"));
        log.warn("  forceFail : {}", message.get("forceFail"));
        log.warn("  ↳ This message was REJECTED or EXPIRED and sent to DLQ");
        log.warn("══════════════════════════════════════════════════");

        store.add(Map.of(
                "consumer", "DeadLetterConsumer",
                "queue", RabbitMQConfig.QUEUE_DLQ,
                "status", "dead-letter",
                "receivedAt", LocalDateTime.now().toString(),
                "data", message
        ));
    }
}
