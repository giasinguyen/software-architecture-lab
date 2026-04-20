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
 * Consumer nhận log có routing_key = "info".
 * Auto-ack: Spring tự xác nhận sau khi method hoàn thành.
 */
@Component
public class InfoLogConsumer {

    private static final Logger log = LoggerFactory.getLogger(InfoLogConsumer.class);
    private final MessageStore store;

    public InfoLogConsumer(MessageStore store) {
        this.store = store;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_INFO)
    public void handleInfoLog(Map<String, Object> message) {
        log.info("══ [INFO  Consumer] ══════════════════════════════");
        log.info("  service   : {}", message.get("service"));
        log.info("  message   : {}", message.get("message"));
        log.info("  timestamp : {}", message.get("timestamp"));
        log.info("══════════════════════════════════════════════════");

        store.add(Map.of(
                "consumer", "InfoLogConsumer",
                "queue", RabbitMQConfig.QUEUE_INFO,
                "status", "processed",
                "receivedAt", LocalDateTime.now().toString(),
                "data", message
        ));
    }
}
