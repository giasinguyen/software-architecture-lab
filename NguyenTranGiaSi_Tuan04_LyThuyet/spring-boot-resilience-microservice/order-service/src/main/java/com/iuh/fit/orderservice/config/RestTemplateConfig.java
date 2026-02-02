package com.iuh.fit.orderservice.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                // Timeout kết nối: 5 giây
                .setConnectTimeout(Duration.ofSeconds(5))
                // Timeout đọc dữ liệu: 10 giây
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }
}
