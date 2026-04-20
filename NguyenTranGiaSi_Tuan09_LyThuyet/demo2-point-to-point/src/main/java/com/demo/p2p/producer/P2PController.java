package com.demo.p2p.producer;

import com.demo.p2p.config.RabbitMQConfig;
import com.demo.p2p.service.MessageStore;
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
@RequestMapping("/api/p2p")
public class P2PController {

    private static final Logger log = LoggerFactory.getLogger(P2PController.class);
    private final RabbitTemplate rabbitTemplate;
    private final MessageStore store;

    public P2PController(RabbitTemplate rabbitTemplate, MessageStore store) {
        this.rabbitTemplate = rabbitTemplate;
        this.store = store;
    }

    /**
     * Hello World – gửi 1 message đơn giản vào queue "hello"
     */
    @PostMapping("/hello")
    public ResponseEntity<Map<String, Object>> sendHello(
            @RequestParam(defaultValue = "Xin chào RabbitMQ!") String message) {

        Map<String, Object> payload = Map.of(
                "message", message,
                "timestamp", LocalDateTime.now().toString()
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_HELLO, payload);

        log.info("[Hello Producer] Sent: {}", message);
        return ResponseEntity.ok(Map.of("status", "sent", "queue", RabbitMQConfig.QUEUE_HELLO, "message", message));
    }

    /**
     * Work Queue – gửi 5 task, 2 worker xử lý song song (round-robin + fair dispatch).
     * Mỗi task chỉ được XỬ LÝ BỞI 1 WORKER duy nhất.
     */
    @PostMapping("/tasks")
    public ResponseEntity<Map<String, Object>> sendBatchTasks() {
        List<String> tasks = List.of(
                "Task 1: Resize image product_001.jpg",
                "Task 2: Generate PDF invoice_2025.pdf",
                "Task 3: Send email to 1000 users",
                "Task 4: Update search index",
                "Task 5: Compress video tutorial.mp4"
        );

        List<Map<String, Object>> sent = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Map<String, Object> payload = Map.of(
                    "taskId", i + 1,
                    "taskName", tasks.get(i),
                    "durationMs", (i + 1) * 1000, // 1s, 2s, 3s, 4s, 5s
                    "timestamp", LocalDateTime.now().toString()
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_TASK, payload);
            log.info("[Work Producer] Sent: {}", tasks.get(i));
            sent.add(payload);
        }

        return ResponseEntity.ok(Map.of(
                "status", "sent",
                "queue", RabbitMQConfig.QUEUE_TASK,
                "taskCount", tasks.size(),
                "tasks", sent,
                "note", "Observe: Worker-1 and Worker-2 take turns processing (round-robin)"
        ));
    }

    /**
     * Gửi 1 task đơn lẻ tùy chỉnh
     */
    @PostMapping("/tasks/single")
    public ResponseEntity<Map<String, Object>> sendSingleTask(
            @RequestParam String taskName,
            @RequestParam(defaultValue = "2000") int durationMs) {

        Map<String, Object> payload = Map.of(
                "taskId", System.currentTimeMillis(),
                "taskName", taskName,
                "durationMs", durationMs,
                "timestamp", LocalDateTime.now().toString()
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_TASK, payload);

        log.info("[Work Producer] Sent single task: {}", taskName);
        return ResponseEntity.ok(Map.of("status", "sent", "queue", RabbitMQConfig.QUEUE_TASK, "task", payload));
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
