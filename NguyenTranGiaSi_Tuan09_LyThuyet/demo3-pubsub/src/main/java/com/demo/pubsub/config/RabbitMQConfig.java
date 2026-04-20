package com.demo.pubsub.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Demo 3 – Pub/Sub
 *
 * ╔══════════════════════════════════════════════════════════╗
 * ║  FANOUT EXCHANGE (order_events)                         ║
 * ║  1 publisher → 3 subscriber đều nhận BẢN SAO            ║
 * ║    ├── fanout.email     → EmailSubscriber               ║
 * ║    ├── fanout.inventory → InventorySubscriber           ║
 * ║    └── fanout.analytics → AnalyticsSubscriber           ║
 * ╠══════════════════════════════════════════════════════════╣
 * ║  TOPIC EXCHANGE (app_events)                            ║
 * ║  Filter theo pattern:                                   ║
 * ║    ├── "orders.#"        → topic.orders (all orders)    ║
 * ║    ├── "*.created.*"     → topic.created (all created)  ║
 * ║    └── "payments.#"      → topic.payments (all payments)║
 * ╚══════════════════════════════════════════════════════════╝
 */
@Configuration
public class RabbitMQConfig {

    // ── Fanout ──────────────────────────────────────────────
    public static final String FANOUT_EXCHANGE = "order_events";
    public static final String FANOUT_QUEUE_EMAIL     = "fanout.email";
    public static final String FANOUT_QUEUE_INVENTORY = "fanout.inventory";
    public static final String FANOUT_QUEUE_ANALYTICS = "fanout.analytics";

    // ── Topic ───────────────────────────────────────────────
    public static final String TOPIC_EXCHANGE = "app_events";
    public static final String TOPIC_QUEUE_ORDERS   = "topic.orders";
    public static final String TOPIC_QUEUE_CREATED  = "topic.created";
    public static final String TOPIC_QUEUE_PAYMENTS = "topic.payments";

    // ────────────────── Fanout Exchange + Queues ─────────────
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE, true, false);
    }

    @Bean
    public Queue fanoutEmailQueue() {
        return QueueBuilder.durable(FANOUT_QUEUE_EMAIL).build();
    }

    @Bean
    public Queue fanoutInventoryQueue() {
        return QueueBuilder.durable(FANOUT_QUEUE_INVENTORY).build();
    }

    @Bean
    public Queue fanoutAnalyticsQueue() {
        return QueueBuilder.durable(FANOUT_QUEUE_ANALYTICS).build();
    }

    @Bean
    public Binding bindEmail() {
        return BindingBuilder.bind(fanoutEmailQueue()).to(fanoutExchange());
    }

    @Bean
    public Binding bindInventory() {
        return BindingBuilder.bind(fanoutInventoryQueue()).to(fanoutExchange());
    }

    @Bean
    public Binding bindAnalytics() {
        return BindingBuilder.bind(fanoutAnalyticsQueue()).to(fanoutExchange());
    }

    // ────────────────── Topic Exchange + Queues ──────────────
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public Queue topicOrdersQueue() {
        return QueueBuilder.durable(TOPIC_QUEUE_ORDERS).build();
    }

    @Bean
    public Queue topicCreatedQueue() {
        return QueueBuilder.durable(TOPIC_QUEUE_CREATED).build();
    }

    @Bean
    public Queue topicPaymentsQueue() {
        return QueueBuilder.durable(TOPIC_QUEUE_PAYMENTS).build();
    }

    /** orders.# → khớp orders.created.vn, orders.shipped.sg, ... */
    @Bean
    public Binding bindOrders() {
        return BindingBuilder.bind(topicOrdersQueue()).to(topicExchange()).with("orders.#");
    }

    /** *.created.* → khớp orders.created.vn, payments.created.sg, ... */
    @Bean
    public Binding bindCreated() {
        return BindingBuilder.bind(topicCreatedQueue()).to(topicExchange()).with("*.created.*");
    }

    /** payments.# → khớp payments.success.vn, payments.failed.vn, ... */
    @Bean
    public Binding bindPayments() {
        return BindingBuilder.bind(topicPaymentsQueue()).to(topicExchange()).with("payments.#");
    }

    // ────────────────── JSON Converter ───────────────────────
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate tpl = new RabbitTemplate(cf);
        tpl.setMessageConverter(jsonMessageConverter());
        return tpl;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf, Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(converter);
        return f;
    }
}
