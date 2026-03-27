package com.demo.functional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// Tắt auto-config DataSource vì chúng ta tự cấu hình 2 datasource thủ công
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class FunctionalApplication {
    public static void main(String[] args) {
        SpringApplication.run(FunctionalApplication.class, args);
    }
}
