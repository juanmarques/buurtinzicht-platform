package com.buurtinzicht;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for the Buurtinzicht backend application.
 * Tests the application context startup and basic configuration.
 */
@SpringBootTest
@ActiveProfiles("test")
class BuurtinzichtBackendApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully
        // and all beans are properly configured
    }
}