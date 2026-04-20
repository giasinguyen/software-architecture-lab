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
 * Hello World Consumer – P2P cơ bản.
 * Nhận message từ queue "hello" và xử lý + manual ACK.
 */
@Component
public class HelloConsumer {

    private static final Logger log = LoggerFactory.getLogger(HelloConsumer.class);
    private final MessageStore store;

    public HelloConsumer(MessageStore store) {
        this.store = store;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_HELLO)
    public void receiveHello(Map<String, Object> message,
                             Channel channel,
                             @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("══ [Hello Consumer] ══════════════════════════════");
        log.info("  message   : {}", message.get("message"));
        log.info("  timestamp : {}", message.get("timestamp"));

        // Manual ACK – xác nhận đã xử lý thành công
        channel.basicAck(deliveryTag, false);
        log.info("  ✓ ACK sent");
        log.info("══════════════════════════════════════════════════");

        store.add(Map.of(
                "consumer", "HelloConsumer",
                "queue", RabbitMQConfig.QUEUE_HELLO,
                "status", "processed (manual ACK)",
                "receivedAt", LocalDateTime.now().toString(),
                "data", message
        ));
    }
}
