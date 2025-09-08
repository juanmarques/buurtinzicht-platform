package com.buurtinzicht.domain.service;

import com.buurtinzicht.domain.model.User;
import com.buurtinzicht.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByKeycloakId(UUID keycloakId) {
        return userRepository.findByKeycloakId(keycloakId);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(UUID keycloakId, String email, String firstName, String lastName) {
        if (userRepository.existsByKeycloakId(keycloakId)) {
            throw new IllegalArgumentException("User with Keycloak ID already exists: " + keycloakId);
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email already exists: " + email);
        }

        User user = new User(keycloakId, email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        
        User savedUser = userRepository.save(user);
        logger.info("Created new user: {} ({})", savedUser.getEmail(), savedUser.getId());
        
        return savedUser;
    }

    public User createOrUpdateUser(UUID keycloakId, String email, String firstName, String lastName) {
        Optional<User> existingUser = findByKeycloakId(keycloakId);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            boolean updated = false;
            
            if (!email.equals(user.getEmail())) {
                user.setEmail(email);
                updated = true;
            }
            
            if (!java.util.Objects.equals(firstName, user.getFirstName())) {
                user.setFirstName(firstName);
                updated = true;
            }
            
            if (!java.util.Objects.equals(lastName, user.getLastName())) {
                user.setLastName(lastName);
                updated = true;
            }
            
            if (updated) {
                user = userRepository.save(user);
                logger.info("Updated user: {} ({})", user.getEmail(), user.getId());
            }
            
            return user;
        } else {
            return createUser(keycloakId, email, firstName, lastName);
        }
    }

    public User updateUser(UUID id, String firstName, String lastName, String preferredLanguage) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPreferredLanguage(preferredLanguage);
        
        User savedUser = userRepository.save(user);
        logger.info("Updated user: {} ({})", savedUser.getEmail(), savedUser.getId());
        
        return savedUser;
    }

    public User updateSubscriptionTier(UUID id, User.SubscriptionTier subscriptionTier) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        
        User.SubscriptionTier oldTier = user.getSubscriptionTier();
        user.setSubscriptionTier(subscriptionTier);
        
        User savedUser = userRepository.save(user);
        logger.info("Updated subscription tier for user {}: {} -> {}", 
            savedUser.getEmail(), oldTier, subscriptionTier);
        
        return savedUser;
    }

    public void updateLastLogin(UUID keycloakId) {
        Optional<User> userOpt = findByKeycloakId(keycloakId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.updateLastLogin();
            userRepository.save(user);
            logger.debug("Updated last login for user: {}", user.getEmail());
        }
    }

    public void deactivateUser(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        
        user.setIsActive(false);
        userRepository.save(user);
        logger.info("Deactivated user: {} ({})", user.getEmail(), user.getId());
    }

    public void reactivateUser(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        
        user.setIsActive(true);
        userRepository.save(user);
        logger.info("Reactivated user: {} ({})", user.getEmail(), user.getId());
    }

    @Transactional(readOnly = true)
    public List<User> findActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<User> findUsersBySubscriptionTier(User.SubscriptionTier tier) {
        return userRepository.findBySubscriptionTier(tier);
    }

    @Transactional(readOnly = true)
    public List<User> findActiveSubscribedUsers() {
        return userRepository.findActiveSubscribedUsers();
    }

    @Transactional(readOnly = true)
    public List<User> findInactiveUsers(int daysSinceLastLogin) {
        Instant cutoffDate = Instant.now().minus(daysSinceLastLogin, ChronoUnit.DAYS);
        return userRepository.findInactiveUsersSince(cutoffDate);
    }

    @Transactional(readOnly = true)
    public List<User> findUsersByLanguage(String language) {
        return userRepository.findByPreferredLanguage(language);
    }

    @Transactional(readOnly = true)
    public long countUsersBySubscriptionTier(User.SubscriptionTier tier) {
        return userRepository.countBySubscriptionTier(tier);
    }

    @Transactional(readOnly = true)
    public boolean userExists(UUID keycloakId) {
        return userRepository.existsByKeycloakId(keycloakId);
    }

    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}