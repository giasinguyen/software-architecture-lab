package com.demo.broker.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Demo 1 – Message Broker
 *
 * Topology:
 *   Producer → [logs.direct] Exchange (Direct)
 *                 ├── routing_key="info"    → queue.info
 *                 ├── routing_key="warning" → queue.warning
 *                 └── routing_key="error"   → queue.error
 *
 *   Khi message bị reject (không requeue):
 *   queue.info / warning / error  →(DLX)→ [logs.dlx] → queue.dead.letters
 */
@Configuration
public class RabbitMQConfig {

    // ── Exchange names ──────────────────────────────────────────
    public static final String DIRECT_EXCHANGE  = "logs.direct";
    public static final String DLX_EXCHANGE     = "logs.dlx";

    // ── Queue names ─────────────────────────────────────────────
    public static final String QUEUE_INFO    = "queue.info";
    public static final String QUEUE_WARNING = "queue.warning";
    public static final String QUEUE_ERROR   = "queue.error";
    public static final String QUEUE_DLQ     = "queue.dead.letters";

    // ── Routing keys ─────────────────────────────────────────────
    public static final String KEY_INFO    = "info";
    public static final String KEY_WARNING = "warning";
    public static final String KEY_ERROR   = "error";
    public static final String KEY_DEAD    = "dead";

    // ── Dead Letter Exchange & Queue ─────────────────────────────
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(QUEUE_DLQ).build();
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(dlxExchange())
                .with(KEY_DEAD);
    }

    // ── Direct Exchange ──────────────────────────────────────────
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }

    // ── Main queues (each configured with DLX fallback) ──────────
    @Bean
    public Queue infoQueue() {
        return QueueBuilder.durable(QUEUE_INFO)
                .withArguments(dlxArgs())
                .build();
    }

    @Bean
    public Queue warningQueue() {
        return QueueBuilder.durable(QUEUE_WARNING)
                .withArguments(dlxArgs())
                .build();
    }

    @Bean
    public Queue errorQueue() {
        return QueueBuilder.durable(QUEUE_ERROR)
                .withArguments(dlxArgs())
                .build();
    }

    /** Common DLX arguments applied to every main queue */
    private Map<String, Object> dlxArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", KEY_DEAD);
        args.put("x-message-ttl", 60_000); // 60 s TTL – expired msg → DLQ
        return args;
    }

    // ── Bindings ─────────────────────────────────────────────────
    @Bean
    public Binding infoBinding() {
        return BindingBuilder.bind(infoQueue()).to(directExchange()).with(KEY_INFO);
    }

    @Bean
    public Binding warningBinding() {
        return BindingBuilder.bind(warningQueue()).to(directExchange()).with(KEY_WARNING);
    }

    @Bean
    public Binding errorBinding() {
        return BindingBuilder.bind(errorQueue()).to(directExchange()).with(KEY_ERROR);
    }

    // ── JSON message converter ────────────────────────────────────
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    /** Listener container factory also uses JSON converter */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setMessageConverter(converter);
        return factory;
    }
}
