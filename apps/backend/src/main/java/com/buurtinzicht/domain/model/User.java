package com.buurtinzicht.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a user in the Buurtinzicht system.
 * Contains basic user information while detailed authentication is handled by Keycloak.
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email", unique = true),
    @Index(name = "idx_users_keycloak_id", columnList = "keycloakId", unique = true),
    @Index(name = "idx_users_subscription_tier", columnList = "subscriptionTier")
})
public class User extends BaseEntity {
    
    @Column(name = "keycloak_id", nullable = false, unique = true)
    private UUID keycloakId;
    
    @Email
    @NotBlank
    @Size(max = 255)
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Size(max = 100)
    @Column(name = "first_name")
    private String firstName;
    
    @Size(max = 100)
    @Column(name = "last_name")
    private String lastName;
    
    @Size(max = 5)
    @Column(name = "preferred_language", nullable = false)
    private String preferredLanguage = "nl"; // nl, fr, en, de
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_tier", nullable = false)
    private SubscriptionTier subscriptionTier = SubscriptionTier.FREE;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "last_login_at")
    private Instant lastLoginAt;
    
    // Constructors
    public User() {
    }
    
    public User(UUID keycloakId, String email) {
        this.keycloakId = keycloakId;
        this.email = email;
    }
    
    // Getters and Setters
    public UUID getKeycloakId() {
        return keycloakId;
    }
    
    public void setKeycloakId(UUID keycloakId) {
        this.keycloakId = keycloakId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPreferredLanguage() {
        return preferredLanguage;
    }
    
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
    
    public SubscriptionTier getSubscriptionTier() {
        return subscriptionTier;
    }
    
    public void setSubscriptionTier(SubscriptionTier subscriptionTier) {
        this.subscriptionTier = subscriptionTier;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Instant getLastLoginAt() {
        return lastLoginAt;
    }
    
    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    
    // Business Methods
    
    /**
     * Returns the user's full name.
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return email; // Fallback to email
        }
    }
    
    /**
     * Checks if user has an active subscription.
     */
    public boolean hasActiveSubscription() {
        return isActive && subscriptionTier != SubscriptionTier.FREE;
    }
    
    /**
     * Updates the last login timestamp.
     */
    public void updateLastLogin() {
        this.lastLoginAt = Instant.now();
    }
    
    /**
     * Enum for subscription tiers
     */
    public enum SubscriptionTier {
        FREE,
        BASIC,       // €9.99/month - 5 reports/month
        PROFESSIONAL, // €14.99/month - Unlimited reports, alerts
        ENTERPRISE   // €49.99/month - API access, bulk exports
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", keycloakId=" + keycloakId +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", preferredLanguage='" + preferredLanguage + '\'' +
                ", subscriptionTier=" + subscriptionTier +
                ", isActive=" + isActive +
                ", lastLoginAt=" + lastLoginAt +
                '}';
    }
}