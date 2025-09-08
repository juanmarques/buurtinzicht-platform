package com.buurtinzicht.web.controller;

import com.buurtinzicht.domain.model.User;
import com.buurtinzicht.domain.repository.UserRepository;
import com.buurtinzicht.web.dto.UserProfileUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private UUID testKeycloakId;
    private String testEmail;
    private User testUser;

    @BeforeEach
    void setUp() {
        testKeycloakId = UUID.randomUUID();
        testEmail = "test@example.com";
        
        testUser = new User(testKeycloakId, testEmail);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser = userRepository.save(testUser);
    }

    @Test
    void getCurrentUserProfile_ShouldReturnProfile_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/profile")
                .with(jwt().jwt(jwt -> jwt
                    .subject(testKeycloakId.toString())
                    .claim("email", testEmail)
                    .claim("given_name", "John")
                    .claim("family_name", "Doe")
                )))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(testEmail))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.subscriptionTier").value("FREE"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.hasActiveSubscription").value(false));
    }

    @Test
    void getCurrentUserProfile_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        UUID nonExistentKeycloakId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/auth/profile")
                .with(jwt().jwt(jwt -> jwt
                    .subject(nonExistentKeycloakId.toString())
                    .claim("email", "nonexistent@example.com")
                )))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCurrentUserProfile_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUserProfile_ShouldUpdateProfile_WhenValidData() throws Exception {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest(
            "Jane", "Smith", "fr"
        );

        mockMvc.perform(put("/api/auth/profile")
                .with(jwt().jwt(jwt -> jwt
                    .subject(testKeycloakId.toString())
                    .claim("email", testEmail)
                ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.preferredLanguage").value("fr"));
    }

    @Test
    void updateUserProfile_ShouldReturnBadRequest_WhenInvalidLanguage() throws Exception {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest(
            "Jane", "Smith", "invalid"
        );

        mockMvc.perform(put("/api/auth/profile")
                .with(jwt().jwt(jwt -> jwt
                    .subject(testKeycloakId.toString())
                    .claim("email", testEmail)
                ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerOrSyncUser_ShouldCreateUser_WhenUserDoesNotExist() throws Exception {
        UUID newKeycloakId = UUID.randomUUID();
        String newEmail = "new@example.com";

        mockMvc.perform(post("/api/auth/register")
                .with(jwt().jwt(jwt -> jwt
                    .subject(newKeycloakId.toString())
                    .claim("email", newEmail)
                    .claim("given_name", "New")
                    .claim("family_name", "User")
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void registerOrSyncUser_ShouldUpdateUser_WhenUserExists() throws Exception {
        String updatedEmail = "updated@example.com";

        mockMvc.perform(post("/api/auth/register")
                .with(jwt().jwt(jwt -> jwt
                    .subject(testKeycloakId.toString())
                    .claim("email", updatedEmail)
                    .claim("given_name", "Updated")
                    .claim("family_name", "User")
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(updatedEmail))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void getSubscriptionInfo_ShouldReturnSubscriptionDetails() throws Exception {
        mockMvc.perform(get("/api/auth/subscription")
                .with(jwt().jwt(jwt -> jwt
                    .subject(testKeycloakId.toString())
                    .claim("email", testEmail)
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier").value("FREE"))
                .andExpect(jsonPath("$.hasActiveSubscription").value(false))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void deactivateAccount_ShouldDeactivateUser() throws Exception {
        mockMvc.perform(delete("/api/auth/profile")
                .with(jwt().jwt(jwt -> jwt
                    .subject(testKeycloakId.toString())
                    .claim("email", testEmail)
                )))
                .andExpect(status().isNoContent());

        User deactivatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertFalse(deactivatedUser.getIsActive());
    }

    @Test
    @WithMockUser(roles = {"B2C_USER"})
    void profileEndpoints_ShouldBeAccessible_WithB2CUserRole() throws Exception {
        mockMvc.perform(get("/api/auth/profile")
                .with(jwt().authorities("ROLE_B2C_USER")
                    .jwt(jwt -> jwt
                        .subject(testKeycloakId.toString())
                        .claim("email", testEmail)
                    )))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"B2B_USER"})
    void profileEndpoints_ShouldBeAccessible_WithB2BUserRole() throws Exception {
        mockMvc.perform(get("/api/auth/profile")
                .with(jwt().authorities("ROLE_B2B_USER")
                    .jwt(jwt -> jwt
                        .subject(testKeycloakId.toString())
                        .claim("email", testEmail)
                    )))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void profileEndpoints_ShouldBeAccessible_WithAdminRole() throws Exception {
        mockMvc.perform(get("/api/auth/profile")
                .with(jwt().authorities("ROLE_ADMIN")
                    .jwt(jwt -> jwt
                        .subject(testKeycloakId.toString())
                        .claim("email", testEmail)
                    )))
                .andExpect(status().isOk());
    }
}