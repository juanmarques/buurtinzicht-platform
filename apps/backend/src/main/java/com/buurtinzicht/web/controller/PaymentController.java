package com.buurtinzicht.web.controller;

import com.buurtinzicht.domain.model.PaymentTransaction;
import com.buurtinzicht.domain.model.SubscriptionPlan;
import com.buurtinzicht.domain.model.UserSubscription;
import com.buurtinzicht.payment.service.StripePaymentService;
import com.buurtinzicht.payment.service.SubscriptionManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for payment processing and subscription management.
 */
@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Payment processing and subscription management")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final SubscriptionManagementService subscriptionService;
    private final StripePaymentService stripePaymentService;

    @Autowired
    public PaymentController(
            SubscriptionManagementService subscriptionService,
            StripePaymentService stripePaymentService) {
        this.subscriptionService = subscriptionService;
        this.stripePaymentService = stripePaymentService;
    }

    @Operation(
        summary = "Get available subscription plans",
        description = "Retrieve all available subscription plans for users to choose from."
    )
    @ApiResponse(responseCode = "200", description = "Plans retrieved successfully")
    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlan>> getAvailablePlans() {
        logger.info("Getting available subscription plans");
        
        List<SubscriptionPlan> plans = subscriptionService.getAvailablePlans();
        return ResponseEntity.ok(plans);
    }

    @Operation(
        summary = "Get user's current subscription",
        description = "Get the authenticated user's current subscription details including usage limits."
    )
    @ApiResponse(responseCode = "200", description = "Subscription retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No active subscription found")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/subscription")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getCurrentSubscription(Authentication authentication) {
        String userId = getUserId(authentication);
        logger.info("Getting current subscription for user: {}", userId);
        
        Optional<UserSubscription> subscription = subscriptionService.getUserSubscription(userId);
        
        if (subscription.isPresent()) {
            UserSubscription sub = subscription.get();
            Map<String, Object> response = Map.of(
                "subscription", sub,
                "remainingScorecardsThisMonth", sub.getRemainingScorecardsThisMonth(),
                "remainingApiCallsToday", sub.getRemainingApiCallsToday(),
                "canAccessPremiumFeatures", subscriptionService.canAccessPremiumFeatures(userId)
            );
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }

    @Operation(
        summary = "Subscribe to a plan",
        description = "Subscribe the authenticated user to a specific subscription plan."
    )
    @ApiResponse(responseCode = "200", description = "Subscription created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid plan or subscription request")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @PostMapping("/subscribe")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<UserSubscription> subscribeToplan(
            @Parameter(description = "Subscription plan ID")
            @Valid @RequestBody Map<String, String> request,
            Authentication authentication) {
        
        String userId = getUserId(authentication);
        String planId = request.get("planId");
        String userEmail = getUserEmail(authentication);
        String userName = getUserName(authentication);
        
        logger.info("User {} subscribing to plan {}", userId, planId);
        
        try {
            UUID planUuid = UUID.fromString(planId);
            UserSubscription subscription = subscriptionService.subscribeUserToPlan(
                userId, planUuid, userEmail, userName);
            
            return ResponseEntity.ok(subscription);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid subscription request for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            logger.error("Error creating subscription for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "Change subscription plan",
        description = "Change the authenticated user's current subscription to a different plan."
    )
    @ApiResponse(responseCode = "200", description = "Plan changed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid plan change request")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @PutMapping("/subscription/change-plan")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<UserSubscription> changePlan(
            @Parameter(description = "New subscription plan ID")
            @Valid @RequestBody Map<String, String> request,
            Authentication authentication) {
        
        String userId = getUserId(authentication);
        String newPlanId = request.get("planId");
        
        logger.info("User {} changing plan to {}", userId, newPlanId);
        
        try {
            UUID planUuid = UUID.fromString(newPlanId);
            UserSubscription subscription = subscriptionService.changePlan(userId, planUuid);
            
            return ResponseEntity.ok(subscription);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid plan change request for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            logger.error("Error changing plan for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "Cancel subscription",
        description = "Cancel the authenticated user's current subscription. By default, cancellation takes effect at the end of the current billing period."
    )
    @ApiResponse(responseCode = "200", description = "Subscription canceled successfully")
    @ApiResponse(responseCode = "400", description = "No active subscription to cancel")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @DeleteMapping("/subscription")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> cancelSubscription(
            @Parameter(description = "Cancel immediately (default: false)")
            @RequestParam(defaultValue = "false") boolean immediately,
            Authentication authentication) {
        
        String userId = getUserId(authentication);
        logger.info("User {} canceling subscription (immediate: {})", userId, immediately);
        
        try {
            subscriptionService.cancelSubscription(userId, immediately);
            
            Map<String, String> response = Map.of(
                "message", immediately ? "Subscription canceled immediately" : "Subscription will be canceled at the end of the current billing period",
                "status", "success"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid cancellation request for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            logger.error("Error canceling subscription for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "Get billing history",
        description = "Retrieve the authenticated user's billing history and payment transactions."
    )
    @ApiResponse(responseCode = "200", description = "Billing history retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/billing-history")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<List<PaymentTransaction>> getBillingHistory(Authentication authentication) {
        String userId = getUserId(authentication);
        logger.info("Getting billing history for user: {}", userId);
        
        List<PaymentTransaction> transactions = subscriptionService.getBillingHistory(userId);
        return ResponseEntity.ok(transactions);
    }

    @Operation(
        summary = "Get customer portal URL",
        description = "Get a URL to the customer billing portal where users can manage their subscription, payment methods, and view invoices."
    )
    @ApiResponse(responseCode = "200", description = "Portal URL generated successfully")
    @ApiResponse(responseCode = "400", description = "No active subscription found")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/customer-portal")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> getCustomerPortalUrl(
            @Parameter(description = "Return URL after portal session")
            @RequestParam(required = false) String returnUrl,
            Authentication authentication) {
        
        String userId = getUserId(authentication);
        logger.info("Getting customer portal URL for user: {}", userId);
        
        try {
            Optional<UserSubscription> subscription = subscriptionService.getUserSubscription(userId);
            
            if (subscription.isPresent() && subscription.get().getStripeCustomerId() != null) {
                String defaultReturnUrl = "http://localhost:3000/dashboard/billing"; // Frontend URL
                String portalUrl = stripePaymentService.createCustomerPortalSession(
                    subscription.get().getStripeCustomerId(),
                    returnUrl != null ? returnUrl : defaultReturnUrl
                );
                
                return ResponseEntity.ok(Map.of("url", portalUrl));
            }
            
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            logger.error("Error getting customer portal URL for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "Check subscription limits",
        description = "Check if the authenticated user can perform specific actions based on their subscription limits."
    )
    @ApiResponse(responseCode = "200", description = "Limits checked successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/limits")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> checkLimits(Authentication authentication) {
        String userId = getUserId(authentication);
        logger.debug("Checking limits for user: {}", userId);
        
        Optional<UserSubscription> subscription = subscriptionService.getUserSubscription(userId);
        
        if (subscription.isPresent()) {
            UserSubscription sub = subscription.get();
            Map<String, Object> limits = Map.of(
                "canMakeScorecardRequest", sub.canMakeScorecardRequest(),
                "canMakeApiCall", sub.canMakeApiCall(),
                "canAccessPremiumFeatures", subscriptionService.canAccessPremiumFeatures(userId),
                "remainingScorecardsThisMonth", sub.getRemainingScorecardsThisMonth(),
                "remainingApiCallsToday", sub.getRemainingApiCallsToday(),
                "maxScorecardRequestsPerMonth", sub.getSubscriptionPlan().getMaxScorecardRequestsPerMonth(),
                "maxApiCallsPerDay", sub.getSubscriptionPlan().getMaxApiCallsPerDay(),
                "planName", sub.getSubscriptionPlan().getName(),
                "planType", sub.getSubscriptionPlan().getPlanType().name()
            );
            
            return ResponseEntity.ok(limits);
        }
        
        return ResponseEntity.notFound().build();
    }

    @Operation(
        summary = "Handle Stripe webhooks",
        description = "Handle webhook events from Stripe for subscription and payment updates. This endpoint is called by Stripe directly."
    )
    @ApiResponse(responseCode = "200", description = "Webhook processed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid webhook payload or signature")
    @PostMapping("/webhooks/stripe")
    public ResponseEntity<Map<String, String>> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature,
            HttpServletRequest request) {
        
        logger.info("Received Stripe webhook");
        
        try {
            StripePaymentService.WebhookHandleResult result = 
                stripePaymentService.handleWebhookEvent(payload, signature);
            
            if (result.isProcessed()) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Webhook processed successfully",
                    "eventType", result.getEventType() != null ? result.getEventType() : "unknown"
                ));
            } else {
                logger.warn("Failed to process Stripe webhook: {}", result.getErrorMessage());
                return ResponseEntity.badRequest().build();
            }
            
        } catch (Exception e) {
            logger.error("Error processing Stripe webhook: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Helper methods

    private String getUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return authentication.getName();
    }

    private String getUserEmail(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }
        return "user@example.com"; // Fallback
    }

    private String getUserName(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String name = jwt.getClaimAsString("name");
            if (name != null) return name;
            
            String givenName = jwt.getClaimAsString("given_name");
            String familyName = jwt.getClaimAsString("family_name");
            if (givenName != null && familyName != null) {
                return givenName + " " + familyName;
            }
            
            return jwt.getClaimAsString("preferred_username");
        }
        return authentication.getName();
    }
}