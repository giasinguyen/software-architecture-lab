package com.demo.functional.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

// ── ORDER DataSource (order_db) ───────────────────────────────────────────────
@Configuration
@EnableJpaRepositories(
    basePackages            = "com.demo.functional.repository.order",
    entityManagerFactoryRef = "orderEntityManagerFactory",
    transactionManagerRef   = "orderTransactionManager"
)
public class OrderDataSourceConfig {

    @Bean(name = "orderDataSource")
    @ConfigurationProperties(prefix = "app.datasource.order")
    public DataSource orderDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "orderEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean orderEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("orderDataSource") DataSource ds) {
        return builder
            .dataSource(ds)
            .packages("com.demo.functional.entity.order")
            .persistenceUnit("orderPU")
            .properties(Map.of(
                "hibernate.hbm2ddl.auto", "none",
                "hibernate.dialect",      "org.hibernate.dialect.MariaDBDialect",
                "hibernate.show_sql",     "true"
            ))
            .build();
    }

    @Bean(name = "orderTransactionManager")
    public PlatformTransactionManager orderTransactionManager(
            @Qualifier("orderEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
