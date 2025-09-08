package com.buurtinzicht.domain.service;

import com.buurtinzicht.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserService userService;

    @Autowired
    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    public Optional<User> getCurrentUser() {
        try {
            UUID keycloakId = getCurrentUserKeycloakId();
            if (keycloakId == null) {
                return Optional.empty();
            }
            
            return userService.findByKeycloakId(keycloakId);
        } catch (Exception e) {
            logger.warn("Failed to get current user: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public UUID getCurrentUserKeycloakId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        
        String subject = jwt.getSubject();
        if (subject == null) {
            return null;
        }
        
        try {
            return UUID.fromString(subject);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid UUID format in JWT subject: {}", subject);
            return null;
        }
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        
        return jwt.getClaimAsString("email");
    }

    public String getCurrentUserFullName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return jwt.getClaimAsString("email");
        }
    }

    public boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isCurrentUserB2BUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> 
                authority.getAuthority().equals("ROLE_B2B_USER") || 
                authority.getAuthority().equals("ROLE_ADMIN")
            );
    }

    public boolean hasCurrentUserRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals(roleWithPrefix));
    }

    public User ensureUserExists() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }
        
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("Invalid authentication token");
        }
        
        UUID keycloakId = UUID.fromString(jwt.getSubject());
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        
        if (email == null) {
            throw new IllegalStateException("Email claim not found in JWT token");
        }
        
        Optional<User> existingUser = userService.findByKeycloakId(keycloakId);
        
        if (existingUser.isPresent()) {
            userService.updateLastLogin(keycloakId);
            return existingUser.get();
        } else {
            User newUser = userService.createOrUpdateUser(keycloakId, email, firstName, lastName);
            userService.updateLastLogin(keycloakId);
            return newUser;
        }
    }
}