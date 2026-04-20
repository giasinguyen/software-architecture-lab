package com.demo.broker.producer;

import com.demo.broker.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * REST API để gửi log message vào Direct Exchange.
 *
 * Demo Direct Exchange routing:
 *   routing_key=info    → queue.info    → InfoLogConsumer
 *   routing_key=warning → queue.warning → WarningLogConsumer
 *   routing_key=error   → queue.error   → ErrorLogConsumer
 *
 * Thử DLQ:
 *   POST /api/broker/send-bad → gửi message có forceFail=true
 *   ErrorLogConsumer sẽ ném AmqpRejectAndDontRequeueException
 *   → message chuyển sang queue.dead.letters
 */
@RestController
@RequestMapping("/api/broker")
public class LogController {

    private static final Logger log = LoggerFactory.getLogger(LogController.class);
    private static final Set<String> VALID_LEVELS = Set.of("info", "warning", "error");

    private final RabbitTemplate rabbitTemplate;

    public LogController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Gửi log message với level chỉ định.
     * Ví dụ: POST /api/broker/send?level=error&message=Payment+failed
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendLog(
            @RequestParam String level,
            @RequestParam String message) {

        String normalizedLevel = level.toLowerCase();
        if (!VALID_LEVELS.contains(normalizedLevel)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid level. Use: info, warning, error"));
        }

        Map<String, Object> payload = buildPayload(normalizedLevel, message, false);
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, normalizedLevel, payload);

        log.info("[Producer] Sent [{}]: {}", normalizedLevel.toUpperCase(), message);
        return ResponseEntity.ok(Map.of(
                "status", "sent",
                "exchange", RabbitMQConfig.DIRECT_EXCHANGE,
                "routingKey", normalizedLevel,
                "message", message
        ));
    }

    /**
     * Gửi message xấu có forceFail=true → ErrorLogConsumer reject → vào DLQ.
     * Dùng để kiểm tra Dead Letter Queue.
     */
    @PostMapping("/send-bad")
    public ResponseEntity<Map<String, Object>> sendBadMessage() {
        Map<String, Object> payload = buildPayload("error",
                "FORCE_FAIL – Intentional bad message for DLQ test", true);

        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, "error", payload);

        log.warn("[Producer] Sent BAD message → will be rejected to DLQ");
        return ResponseEntity.ok(Map.of(
                "status", "sent",
                "note", "ErrorLogConsumer will REJECT this → it goes to queue.dead.letters",
                "targetDLQ", RabbitMQConfig.QUEUE_DLQ
        ));
    }

    private Map<String, Object> buildPayload(String level, String message, boolean forceFail) {
        return Map.of(
                "level", level,
                "message", message,
                "service", "order-service",
                "timestamp", LocalDateTime.now().toString(),
                "forceFail", forceFail
        );
    }
}
