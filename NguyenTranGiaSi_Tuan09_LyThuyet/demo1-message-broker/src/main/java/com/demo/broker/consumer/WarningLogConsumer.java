package com.demo.broker.consumer;

import com.demo.broker.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumer nhận log có routing_key = "warning".
 * Auto-ack: Spring tự xác nhận sau khi method hoàn thành.
 */
@Component
public class WarningLogConsumer {

    private static final Logger log = LoggerFactory.getLogger(WarningLogConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_WARNING)
    public void handleWarningLog(Map<String, Object> message) {
        log.warn("══ [WARNING Consumer] ════════════════════════════");
        log.warn("  service   : {}", message.get("service"));
        log.warn("  message   : {}", message.get("message"));
        log.warn("  timestamp : {}", message.get("timestamp"));
        log.warn("══════════════════════════════════════════════════");
    }
}
