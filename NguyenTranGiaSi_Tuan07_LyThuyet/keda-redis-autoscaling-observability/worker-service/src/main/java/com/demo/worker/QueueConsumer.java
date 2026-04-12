package com.demo.worker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class QueueConsumer {

    private static final Logger log = LoggerFactory.getLogger(QueueConsumer.class);

    private final StringRedisTemplate redis;
    private final Counter jobsProcessed;

    @Value("${worker.queue-name}")
    private String queueName;

    @Value("${worker.processing-delay-ms}")
    private long processingDelay;

    public QueueConsumer(StringRedisTemplate redis, MeterRegistry registry) {
        this.redis = redis;
        this.jobsProcessed = Counter.builder("worker.jobs.processed")
                .description("Total jobs processed")
                .register(registry);
    }

    @Scheduled(fixedDelay = 100)
    public void consumeJob() {
        String job = redis.opsForList().leftPop(queueName);
        if (job == null) return;

        log.info("Processing: {}", job);
        try {
            Thread.sleep(processingDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        jobsProcessed.increment();
        log.info("Completed: {}", job);
    }
}
