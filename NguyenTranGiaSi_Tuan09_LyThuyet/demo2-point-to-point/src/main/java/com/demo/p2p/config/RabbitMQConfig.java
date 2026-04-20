package com.demo.p2p.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Demo 2 – Point-to-Point topology
 *
 *   Producer → [hello]      queue → HelloConsumer          (basic P2P)
 *   Producer → [task_queue]  queue → Worker-1, Worker-2    (Work Queue, round-robin + fair dispatch)
 *
 * Đặc điểm P2P: mỗi message chỉ được XỬ LÝ BỞI 1 consumer duy nhất.
 */
@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_HELLO = "hello";
    public static final String QUEUE_TASK  = "task_queue";

    @Bean
    public Queue helloQueue() {
        return QueueBuilder.durable(QUEUE_HELLO).build();
    }

    @Bean
    public Queue taskQueue() {
        return QueueBuilder.durable(QUEUE_TASK).build();
    }

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
            ConnectionFactory cf,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setMessageConverter(converter);
        factory.setPrefetchCount(1);                   // Fair dispatch
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
