package com.buurtinzicht.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Health check controller for the Buurtinzicht backend service.
 * Provides basic health information and system status.
 */
@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController implements HealthIndicator {
    
    @Operation(
        summary = "Get application health status",
        description = "Returns the current health status of the application"
    )
    @ApiResponse(responseCode = "200", description = "Application is healthy")
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", Instant.now(),
            "service", "buurtinzicht-backend",
            "version", "1.0.0-SNAPSHOT"
        ));
    }
    
    @Operation(
        summary = "Get application information",
        description = "Returns basic information about the application"
    )
    @ApiResponse(responseCode = "200", description = "Application information")
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
            "name", "Buurtinzicht Backend API",
            "description", "Neighborhood Insights Platform for Belgium",
            "version", "1.0.0-SNAPSHOT",
            "profiles", System.getProperty("spring.profiles.active", "default"),
            "java", System.getProperty("java.version"),
            "timestamp", Instant.now()
        ));
    }
    
    @Override
    public Health health() {
        return Health.up()
                .withDetail("service", "buurtinzicht-backend")
                .withDetail("status", "UP")
                .withDetail("timestamp", Instant.now())
                .build();
    }
}