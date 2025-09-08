package com.buurtinzicht.web.controller;

import com.buurtinzicht.domain.model.User;
import com.buurtinzicht.domain.service.UserService;
import com.buurtinzicht.web.dto.UserDTO;
import com.buurtinzicht.web.dto.UserProfileUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and profile management")
@SecurityRequirement(name = "Bearer Authentication")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Get current user profile",
        description = "Returns the profile information of the currently authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "User profile retrieved successfully")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "404", description = "User profile not found")
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        UUID keycloakId = getCurrentUserKeycloakId();
        
        Optional<User> userOpt = userService.findByKeycloakId(keycloakId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        userService.updateLastLogin(keycloakId);
        
        return ResponseEntity.ok(convertToDTO(user));
    }

    @Operation(
        summary = "Update user profile",
        description = "Updates the profile information of the currently authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "User profile updated successfully")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "404", description = "User profile not found")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> updateUserProfile(
            @Valid @RequestBody UserProfileUpdateRequest updateRequest) {
        
        UUID keycloakId = getCurrentUserKeycloakId();
        
        Optional<User> userOpt = userService.findByKeycloakId(keycloakId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        User updatedUser = userService.updateUser(
            user.getId(),
            updateRequest.getFirstName(),
            updateRequest.getLastName(),
            updateRequest.getPreferredLanguage()
        );
        
        return ResponseEntity.ok(convertToDTO(updatedUser));
    }

    @Operation(
        summary = "Register or sync user",
        description = "Creates a new user or syncs existing user information from Keycloak"
    )
    @ApiResponse(responseCode = "200", description = "User registered/synced successfully")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> registerOrSyncUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        
        UUID keycloakId = UUID.fromString(jwt.getSubject());
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        
        User user = userService.createOrUpdateUser(keycloakId, email, firstName, lastName);
        userService.updateLastLogin(keycloakId);
        
        logger.info("User registered/synced: {} ({})", user.getEmail(), user.getId());
        
        return ResponseEntity.ok(convertToDTO(user));
    }

    @Operation(
        summary = "Get user subscription info",
        description = "Returns subscription information for the current user"
    )
    @ApiResponse(responseCode = "200", description = "Subscription info retrieved successfully")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/subscription")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getSubscriptionInfo() {
        UUID keycloakId = getCurrentUserKeycloakId();
        
        Optional<User> userOpt = userService.findByKeycloakId(keycloakId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        
        Map<String, Object> subscriptionInfo = Map.of(
            "tier", user.getSubscriptionTier(),
            "hasActiveSubscription", user.hasActiveSubscription(),
            "isActive", user.getIsActive()
        );
        
        return ResponseEntity.ok(subscriptionInfo);
    }

    @Operation(
        summary = "Delete user account",
        description = "Deactivates the current user's account"
    )
    @ApiResponse(responseCode = "204", description = "Account deactivated successfully")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "404", description = "User not found")
    @DeleteMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deactivateAccount() {
        UUID keycloakId = getCurrentUserKeycloakId();
        
        Optional<User> userOpt = userService.findByKeycloakId(keycloakId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        userService.deactivateUser(user.getId());
        
        logger.info("User account deactivated: {} ({})", user.getEmail(), user.getId());
        
        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserKeycloakId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return UUID.fromString(jwt.getSubject());
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getKeycloakId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getFullName(),
            user.getPreferredLanguage(),
            user.getSubscriptionTier(),
            user.getIsActive(),
            user.getLastLoginAt(),
            user.hasActiveSubscription(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}