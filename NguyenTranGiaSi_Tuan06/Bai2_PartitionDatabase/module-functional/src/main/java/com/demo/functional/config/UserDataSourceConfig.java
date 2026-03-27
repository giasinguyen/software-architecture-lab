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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

// ── USER DataSource (user_db) ─────────────────────────────────────────────────
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages            = "com.demo.functional.repository.user",
    entityManagerFactoryRef = "userEntityManagerFactory",
    transactionManagerRef   = "userTransactionManager"
)
public class UserDataSourceConfig {

    @Primary
    @Bean(name = "userDataSource")
    @ConfigurationProperties(prefix = "app.datasource.user")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "userEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean userEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("userDataSource") DataSource ds) {
        return builder
            .dataSource(ds)
            .packages("com.demo.functional.entity.user")
            .persistenceUnit("userPU")
            .properties(Map.of(
                "hibernate.hbm2ddl.auto", "none",
                "hibernate.dialect",      "org.hibernate.dialect.MariaDBDialect",
                "hibernate.show_sql",     "true"
            ))
            .build();
    }

    @Primary
    @Bean(name = "userTransactionManager")
    public PlatformTransactionManager userTransactionManager(
            @Qualifier("userEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
