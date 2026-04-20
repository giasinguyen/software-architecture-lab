package com.demo.pubsub.producer;

import com.demo.pubsub.config.RabbitMQConfig;
import com.demo.pubsub.service.MessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pubsub")
public class PubSubController {

    private static final Logger log = LoggerFactory.getLogger(PubSubController.class);
    private final RabbitTemplate rabbitTemplate;
    private final MessageStore store;

    public PubSubController(RabbitTemplate rabbitTemplate, MessageStore store) {
        this.rabbitTemplate = rabbitTemplate;
        this.store = store;
    }

    /**
     * FANOUT – Broadcast order event tới TẤT CẢ subscriber (Email, Inventory, Analytics).
     * Routing key bị bỏ qua, mọi queue bind tới fanout exchange đều nhận.
     */
    @PostMapping("/fanout")
    public ResponseEntity<Map<String, Object>> publishFanout() {
        Map<String, Object> payload = Map.of(
                "event", "order.created",
                "orderId", "ORD-" + System.currentTimeMillis() % 10000,
                "total", 500000,
                "currency", "VND",
                "timestamp", LocalDateTime.now().toString()
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", payload);

        log.info("[Fanout Publisher] Broadcast order event → ALL 3 subscribers receive this");
        return ResponseEntity.ok(Map.of(
                "status", "broadcast",
                "exchange", RabbitMQConfig.FANOUT_EXCHANGE,
                "type", "fanout",
                "payload", payload,
                "note", "All 3 subscribers (Email, Inventory, Analytics) receive a COPY"
        ));
    }

    /**
     * TOPIC – Gửi event theo routing key, consumer subscribe theo pattern.
     * Ví dụ routing keys:
     *   orders.created.vn  → khớp "orders.#" và "*.created.*"
     *   payments.failed.vn → khớp "payments.#"
     *   orders.shipped.sg  → khớp "orders.#"
     */
    @PostMapping("/topic")
    public ResponseEntity<Map<String, Object>> publishTopic(
            @RequestParam String routingKey,
            @RequestParam(defaultValue = "Event data") String message) {

        Map<String, Object> payload = Map.of(
                "routingKey", routingKey,
                "message", message,
                "timestamp", LocalDateTime.now().toString()
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, routingKey, payload);

        log.info("[Topic Publisher] Sent [{}]: {}", routingKey, message);
        return ResponseEntity.ok(Map.of(
                "status", "sent",
                "exchange", RabbitMQConfig.TOPIC_EXCHANGE,
                "type", "topic",
                "routingKey", routingKey,
                "payload", payload,
                "matchingPatterns", describeMatches(routingKey)
        ));
    }

    /**
     * SIMULATE – Gửi 5 event khác nhau để quan sát Topic routing.
     */
    @PostMapping("/simulate")
    public ResponseEntity<Map<String, Object>> simulateEvents() {
        List<Map<String, String>> events = List.of(
                Map.of("key", "orders.created.vn",   "msg", "Order ORD-1 created in VN"),
                Map.of("key", "orders.shipped.vn",   "msg", "Order ORD-2 shipped in VN"),
                Map.of("key", "orders.created.sg",   "msg", "Order ORD-3 created in SG"),
                Map.of("key", "payments.success.vn", "msg", "Payment PAY-1 success"),
                Map.of("key", "payments.failed.vn",  "msg", "Payment PAY-2 failed – insufficient funds")
        );

        List<Map<String, Object>> results = new ArrayList<>();
        for (var e : events) {
            Map<String, Object> payload = Map.of(
                    "routingKey", e.get("key"),
                    "message", e.get("msg"),
                    "timestamp", LocalDateTime.now().toString()
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, e.get("key"), payload);
            log.info("[Topic Publisher] Sent [{}]: {}", e.get("key"), e.get("msg"));
            results.add(Map.of(
                    "routingKey", e.get("key"),
                    "message", e.get("msg"),
                    "matchedBy", describeMatches(e.get("key"))
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", "simulated",
                "eventCount", events.size(),
                "events", results
        ));
    }

    private List<String> describeMatches(String routingKey) {
        List<String> matches = new ArrayList<>();
        // orders.#
        if (routingKey.startsWith("orders.")) matches.add("topic.orders (orders.#)");
        // *.created.*
        String[] parts = routingKey.split("\\.");
        if (parts.length >= 2 && "created".equals(parts[1])) matches.add("topic.created (*.created.*)");
        // payments.#
        if (routingKey.startsWith("payments.")) matches.add("topic.payments (payments.#)");
        return matches;
    }

    @GetMapping("/received")
    public ResponseEntity<Map<String, Object>> getReceived() {
        return ResponseEntity.ok(Map.of(
                "total", store.size(),
                "messages", store.getAll()
        ));
    }

    @DeleteMapping("/received/clear")
    public ResponseEntity<Map<String, Object>> clearReceived() {
        store.clear();
        return ResponseEntity.ok(Map.of("status", "cleared"));
    }
}
