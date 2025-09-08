package com.buurtinzicht.domain.service;

import com.buurtinzicht.domain.model.User;
import com.buurtinzicht.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UUID testKeycloakId;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testKeycloakId = UUID.randomUUID();
        testEmail = "test@example.com";
    }

    @Test
    void createUser_ShouldCreateNewUser_WhenValidData() {
        User user = userService.createUser(testKeycloakId, testEmail, "John", "Doe");

        assertNotNull(user.getId());
        assertEquals(testKeycloakId, user.getKeycloakId());
        assertEquals(testEmail, user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(User.SubscriptionTier.FREE, user.getSubscriptionTier());
        assertTrue(user.getIsActive());
        assertEquals("nl", user.getPreferredLanguage());
    }

    @Test
    void createUser_ShouldThrowException_WhenKeycloakIdExists() {
        userService.createUser(testKeycloakId, testEmail, "John", "Doe");

        assertThrows(IllegalArgumentException.class, () -> 
            userService.createUser(testKeycloakId, "another@example.com", "Jane", "Smith")
        );
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        userService.createUser(testKeycloakId, testEmail, "John", "Doe");

        assertThrows(IllegalArgumentException.class, () -> 
            userService.createUser(UUID.randomUUID(), testEmail, "Jane", "Smith")
        );
    }

    @Test
    void createOrUpdateUser_ShouldCreateUser_WhenUserDoesNotExist() {
        User user = userService.createOrUpdateUser(testKeycloakId, testEmail, "John", "Doe");

        assertNotNull(user.getId());
        assertEquals(testKeycloakId, user.getKeycloakId());
        assertEquals(testEmail, user.getEmail());
    }

    @Test
    void createOrUpdateUser_ShouldUpdateUser_WhenUserExists() {
        User originalUser = userService.createUser(testKeycloakId, testEmail, "John", "Doe");
        
        User updatedUser = userService.createOrUpdateUser(
            testKeycloakId, "updated@example.com", "Jane", "Smith"
        );

        assertEquals(originalUser.getId(), updatedUser.getId());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("Jane", updatedUser.getFirstName());
        assertEquals("Smith", updatedUser.getLastName());
    }

    @Test
    void findByKeycloakId_ShouldReturnUser_WhenExists() {
        User createdUser = userService.createUser(testKeycloakId, testEmail, "John", "Doe");

        Optional<User> foundUser = userService.findByKeycloakId(testKeycloakId);

        assertTrue(foundUser.isPresent());
        assertEquals(createdUser.getId(), foundUser.get().getId());
    }

    @Test
    void findByKeycloakId_ShouldReturnEmpty_WhenNotExists() {
        Optional<User> foundUser = userService.findByKeycloakId(UUID.randomUUID());

        assertTrue(foundUser.isEmpty());
    }

    @Test
    void updateSubscriptionTier_ShouldUpdateTier_WhenUserExists() {
        User user = userService.createUser(testKeycloakId, testEmail, "John", "Doe");
        
        User updatedUser = userService.updateSubscriptionTier(user.getId(), User.SubscriptionTier.PROFESSIONAL);

        assertEquals(User.SubscriptionTier.PROFESSIONAL, updatedUser.getSubscriptionTier());
        assertTrue(updatedUser.hasActiveSubscription());
    }

    @Test
    void updateSubscriptionTier_ShouldThrowException_WhenUserNotExists() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(EntityNotFoundException.class, () -> 
            userService.updateSubscriptionTier(nonExistentId, User.SubscriptionTier.PROFESSIONAL)
        );
    }

    @Test
    void deactivateUser_ShouldSetActiveToFalse() {
        User user = userService.createUser(testKeycloakId, testEmail, "John", "Doe");
        
        userService.deactivateUser(user.getId());
        
        User deactivatedUser = userService.findById(user.getId()).orElseThrow();
        assertFalse(deactivatedUser.getIsActive());
    }

    @Test
    void reactivateUser_ShouldSetActiveToTrue() {
        User user = userService.createUser(testKeycloakId, testEmail, "John", "Doe");
        userService.deactivateUser(user.getId());
        
        userService.reactivateUser(user.getId());
        
        User reactivatedUser = userService.findById(user.getId()).orElseThrow();
        assertTrue(reactivatedUser.getIsActive());
    }

    @Test
    void updateLastLogin_ShouldUpdateTimestamp() {
        User user = userService.createUser(testKeycloakId, testEmail, "John", "Doe");
        Instant beforeUpdate = Instant.now();
        
        userService.updateLastLogin(testKeycloakId);
        
        User updatedUser = userService.findById(user.getId()).orElseThrow();
        assertNotNull(updatedUser.getLastLoginAt());
        assertTrue(updatedUser.getLastLoginAt().isAfter(beforeUpdate));
    }

    @Test
    void findActiveUsers_ShouldReturnOnlyActiveUsers() {
        User activeUser = userService.createUser(UUID.randomUUID(), "active@example.com", "Active", "User");
        User inactiveUser = userService.createUser(UUID.randomUUID(), "inactive@example.com", "Inactive", "User");
        userService.deactivateUser(inactiveUser.getId());
        
        List<User> activeUsers = userService.findActiveUsers();
        
        assertTrue(activeUsers.stream().anyMatch(u -> u.getId().equals(activeUser.getId())));
        assertTrue(activeUsers.stream().noneMatch(u -> u.getId().equals(inactiveUser.getId())));
    }

    @Test
    void findUsersBySubscriptionTier_ShouldReturnCorrectUsers() {
        User freeUser = userService.createUser(UUID.randomUUID(), "free@example.com", "Free", "User");
        User proUser = userService.createUser(UUID.randomUUID(), "pro@example.com", "Pro", "User");
        userService.updateSubscriptionTier(proUser.getId(), User.SubscriptionTier.PROFESSIONAL);
        
        List<User> freeUsers = userService.findUsersBySubscriptionTier(User.SubscriptionTier.FREE);
        List<User> proUsers = userService.findUsersBySubscriptionTier(User.SubscriptionTier.PROFESSIONAL);
        
        assertTrue(freeUsers.stream().anyMatch(u -> u.getId().equals(freeUser.getId())));
        assertTrue(proUsers.stream().anyMatch(u -> u.getId().equals(proUser.getId())));
    }

    @Test
    void findInactiveUsers_ShouldReturnUsersNotLoggedInRecently() {
        User recentUser = userService.createUser(UUID.randomUUID(), "recent@example.com", "Recent", "User");
        User oldUser = userService.createUser(UUID.randomUUID(), "old@example.com", "Old", "User");
        
        oldUser.setLastLoginAt(Instant.now().minus(10, ChronoUnit.DAYS));
        userRepository.save(oldUser);
        
        userService.updateLastLogin(recentUser.getKeycloakId());
        
        List<User> inactiveUsers = userService.findInactiveUsers(7);
        
        assertTrue(inactiveUsers.stream().anyMatch(u -> u.getId().equals(oldUser.getId())));
        assertTrue(inactiveUsers.stream().noneMatch(u -> u.getId().equals(recentUser.getId())));
    }

    @Test
    void userExists_ShouldReturnCorrectBoolean() {
        assertFalse(userService.userExists(testKeycloakId));
        
        userService.createUser(testKeycloakId, testEmail, "John", "Doe");
        
        assertTrue(userService.userExists(testKeycloakId));
    }

    @Test
    void emailExists_ShouldReturnCorrectBoolean() {
        assertFalse(userService.emailExists(testEmail));
        
        userService.createUser(testKeycloakId, testEmail, "John", "Doe");
        
        assertTrue(userService.emailExists(testEmail));
    }
}