package com.demo.broker.consumer;

import com.demo.broker.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumer nhận log có routing_key = "error".
 *
 * Khi message có forceFail=true:
 *   → ném AmqpRejectAndDontRequeueException
 *   → Spring AMQP nack message với requeue=false
 *   → RabbitMQ chuyển message sang Dead Letter Exchange (logs.dlx)
 *   → queue.dead.letters nhận message
 *
 * Đây là cơ chế DLQ routing chuẩn trong Spring AMQP.
 */
@Component
public class ErrorLogConsumer {

    private static final Logger log = LoggerFactory.getLogger(ErrorLogConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ERROR)
    public void handleErrorLog(Map<String, Object> message) {
        boolean forceFail = Boolean.TRUE.equals(message.get("forceFail"));

        if (forceFail) {
            log.error("══ [ERROR Consumer] REJECTING message → DLQ ══════");
            log.error("  message   : {}", message.get("message"));
            log.error("  → Sending to queue.dead.letters via DLX");
            log.error("══════════════════════════════════════════════════");

            // Ném exception đặc biệt: Spring AMQP sẽ nack(requeue=false)
            // → message đi vào Dead Letter Exchange thay vì requeue
            throw new AmqpRejectAndDontRequeueException(
                    "Intentional rejection for DLQ demo: " + message.get("message"));
        }

        log.error("══ [ERROR Consumer] ══════════════════════════════");
        log.error("  service   : {}", message.get("service"));
        log.error("  message   : {}", message.get("message"));
        log.error("  timestamp : {}", message.get("timestamp"));
        log.error("══════════════════════════════════════════════════");
    }
}
