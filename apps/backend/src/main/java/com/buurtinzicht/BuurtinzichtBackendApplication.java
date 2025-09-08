package com.buurtinzicht;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the Buurtinzicht backend service.
 * 
 * This application provides comprehensive neighborhood insights for Belgian properties,
 * including data aggregation from government APIs, spatial analysis, and predictive analytics.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableKafka
@EnableTransactionManagement
public class BuurtinzichtBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuurtinzichtBackendApplication.class, args);
    }
}