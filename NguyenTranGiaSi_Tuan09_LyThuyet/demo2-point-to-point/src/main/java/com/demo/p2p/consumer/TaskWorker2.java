package com.demo.p2p.consumer;

import com.demo.p2p.config.RabbitMQConfig;
import com.demo.p2p.service.MessageStore;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Work Queue – Worker 2.
 *
 * Cùng listen trên queue "task_queue" với Worker1.
 * Chứng minh P2P: mỗi message chỉ đến ĐÚNG 1 worker.
 */
@Component
public class TaskWorker2 {

    private static final Logger log = LoggerFactory.getLogger(TaskWorker2.class);
    private final MessageStore store;

    public TaskWorker2(MessageStore store) {
        this.store = store;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_TASK)
    public void processTask(Map<String, Object> task,
                            Channel channel,
                            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        String taskName = String.valueOf(task.get("taskName"));
        int durationMs = ((Number) task.get("durationMs")).intValue();

        log.info("══ [Worker-2] ════════════════════════════════════");
        log.info("  taskId    : {}", task.get("taskId"));
        log.info("  taskName  : {}", taskName);
        log.info("  Processing... ({} ms)", durationMs);

        try {
            Thread.sleep(durationMs); // Giả lập thời gian xử lý
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        channel.basicAck(deliveryTag, false);
        log.info("  ✓ Done! ACK sent");
        log.info("══════════════════════════════════════════════════");

        store.add(Map.of(
                "consumer", "Worker-2",
                "queue", RabbitMQConfig.QUEUE_TASK,
                "status", "completed in " + durationMs + "ms",
                "receivedAt", LocalDateTime.now().toString(),
                "data", task
        ));
    }
}
