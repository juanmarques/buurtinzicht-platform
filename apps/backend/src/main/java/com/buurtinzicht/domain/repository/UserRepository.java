package com.buurtinzicht.domain.repository;

import com.buurtinzicht.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByKeycloakId(UUID keycloakId);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByIsActiveTrue();
    
    List<User> findBySubscriptionTier(User.SubscriptionTier subscriptionTier);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.subscriptionTier != com.buurtinzicht.domain.model.User$SubscriptionTier.FREE")
    List<User> findActiveSubscribedUsers();
    
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :cutoffDate")
    List<User> findInactiveUsersSince(@Param("cutoffDate") Instant cutoffDate);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.subscriptionTier = :tier")
    long countBySubscriptionTier(@Param("tier") User.SubscriptionTier tier);
    
    @Query("SELECT u FROM User u WHERE u.preferredLanguage = :language")
    List<User> findByPreferredLanguage(@Param("language") String language);
    
    boolean existsByKeycloakId(UUID keycloakId);
    
    boolean existsByEmail(String email);
}