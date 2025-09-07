package com.buurtinzicht.web.dto;

import com.buurtinzicht.domain.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "User data transfer object")
public record UserDTO(
    @Schema(description = "User's unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID id,
    
    @Schema(description = "Keycloak user identifier", example = "550e8400-e29b-41d4-a716-446655440001")
    UUID keycloakId,
    
    @Schema(description = "User's email address", example = "john.doe@example.com")
    String email,
    
    @Schema(description = "User's first name", example = "John")
    String firstName,
    
    @Schema(description = "User's last name", example = "Doe")
    String lastName,
    
    @Schema(description = "User's full name", example = "John Doe")
    String fullName,
    
    @Schema(description = "User's preferred language", example = "nl", allowableValues = {"nl", "fr", "en", "de"})
    String preferredLanguage,
    
    @Schema(description = "User's subscription tier", example = "FREE")
    User.SubscriptionTier subscriptionTier,
    
    @Schema(description = "Whether the user account is active", example = "true")
    Boolean isActive,
    
    @Schema(description = "Timestamp of last login", example = "2025-01-15T10:30:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant lastLoginAt,
    
    @Schema(description = "Whether the user has an active paid subscription", example = "false")
    Boolean hasActiveSubscription,
    
    @Schema(description = "Account creation timestamp", example = "2025-01-01T12:00:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant createdAt,
    
    @Schema(description = "Last account update timestamp", example = "2025-01-15T10:30:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant updatedAt
) {}