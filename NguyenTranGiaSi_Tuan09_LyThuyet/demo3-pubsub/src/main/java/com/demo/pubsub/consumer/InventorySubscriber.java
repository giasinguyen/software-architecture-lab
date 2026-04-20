package com.demo.pubsub.consumer;

import com.demo.pubsub.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Fanout Subscriber – Inventory Service.
 * Nhận BẢN SAO của mọi message gửi tới fanout exchange (order_events).
 */
@Component
public class InventorySubscriber {

    private static final Logger log = LoggerFactory.getLogger(InventorySubscriber.class);

    @RabbitListener(queues = RabbitMQConfig.FANOUT_QUEUE_INVENTORY)
    public void onOrderEvent(Map<String, Object> event) {
        log.info("📦 ══ [Inventory Subscriber] ══════════════════════");
        log.info("  event     : {}", event.get("event"));
        log.info("  orderId   : {}", event.get("orderId"));
        log.info("  → Deducting stock for ordered items...");
        log.info("══════════════════════════════════════════════════");
    }
}
