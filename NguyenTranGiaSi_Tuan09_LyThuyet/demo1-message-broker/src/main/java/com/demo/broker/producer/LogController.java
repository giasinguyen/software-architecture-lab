package com.demo.broker.producer;

import com.demo.broker.config.RabbitMQConfig;
import com.demo.broker.service.MessageStore;
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
    private final MessageStore store;

    public LogController(RabbitTemplate rabbitTemplate, MessageStore store) {
        this.rabbitTemplate = rabbitTemplate;
        this.store = store;
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

    /**
     * Xem tất cả message đã được consumer xử lý (mới nhất lên đầu).
     * GET /api/broker/received
     */
    @GetMapping("/received")
    public ResponseEntity<Map<String, Object>> getReceived() {
        var messages = store.getAll();
        return ResponseEntity.ok(Map.of(
                "total", messages.size(),
                "messages", messages
        ));
    }

    /**
     * Xóa lịch sử để test lại từ đầu.
     * DELETE /api/broker/received/clear
     */
    @DeleteMapping("/received/clear")
    public ResponseEntity<Map<String, Object>> clearReceived() {
        store.clear();
        return ResponseEntity.ok(Map.of("status", "cleared"));
    }
}
