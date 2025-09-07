package com.buurtinzicht.payment.service;

import com.buurtinzicht.domain.model.PaymentTransaction;
import com.buurtinzicht.domain.model.SubscriptionPlan;
import com.buurtinzicht.domain.model.UserSubscription;
import com.buurtinzicht.domain.repository.SubscriptionPlanRepository;
import com.buurtinzicht.domain.repository.UserSubscriptionRepository;
import com.buurtinzicht.domain.repository.PaymentTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing user subscriptions and billing.
 */
@Service
@Transactional
public class SubscriptionManagementService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionManagementService.class);

    private final UserSubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final StripePaymentService stripePaymentService;

    @Autowired
    public SubscriptionManagementService(
            UserSubscriptionRepository subscriptionRepository,
            SubscriptionPlanRepository planRepository,
            PaymentTransactionRepository transactionRepository,
            StripePaymentService stripePaymentService) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.transactionRepository = transactionRepository;
        this.stripePaymentService = stripePaymentService;
    }

    /**
     * Get user's current active subscription.
     */
    @Cacheable(value = "userSubscriptions", key = "#userId")
    public Optional<UserSubscription> getUserSubscription(String userId) {
        logger.debug("Getting subscription for user: {}", userId);
        
        return subscriptionRepository.findByUserIdAndStatus(userId, UserSubscription.SubscriptionStatus.ACTIVE)
            .or(() -> subscriptionRepository.findByUserIdAndStatus(userId, UserSubscription.SubscriptionStatus.TRIALING))
            .or(() -> {
                // If no active subscription, create free subscription
                logger.info("No active subscription found for user {}, creating free subscription", userId);
                return Optional.of(createFreeSubscription(userId));
            });
    }

    /**
     * Get all available subscription plans.
     */
    @Cacheable(value = "subscriptionPlans")
    public List<SubscriptionPlan> getAvailablePlans() {
        logger.debug("Getting all available subscription plans");
        
        return planRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    /**
     * Get subscription plan by ID.
     */
    @Cacheable(value = "subscriptionPlans", key = "#planId")
    public Optional<SubscriptionPlan> getPlan(UUID planId) {
        logger.debug("Getting subscription plan: {}", planId);
        
        return planRepository.findByIdAndIsActiveTrue(planId);
    }

    /**
     * Subscribe user to a plan.
     */
    public UserSubscription subscribeUserToPlan(String userId, UUID planId, String userEmail, String userName) {
        logger.info("Subscribing user {} to plan {}", userId, planId);
        
        try {
            // Get the plan
            SubscriptionPlan plan = planRepository.findByIdAndIsActiveTrue(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found or inactive: " + planId));

            // Cancel any existing subscription
            Optional<UserSubscription> existingSubscription = getUserSubscription(userId);
            if (existingSubscription.isPresent() && !existingSubscription.get().getSubscriptionPlan().isFreePlan()) {
                cancelSubscription(userId, false);
            }

            // Create new subscription
            UserSubscription subscription = new UserSubscription(userId, plan);

            // If not a free plan, set up Stripe subscription
            if (!plan.isFreePlan()) {
                // Create or get Stripe customer
                String customerId = getOrCreateStripeCustomer(userId, userEmail, userName);
                subscription.setStripeCustomerId(customerId);

                // Create Stripe subscription
                String stripeSubscriptionId = stripePaymentService.createStripeSubscription(customerId, plan);
                subscription.setStripeSubscriptionId(stripeSubscriptionId);

                // Create initial transaction record
                PaymentTransaction transaction = new PaymentTransaction(
                    subscription, 
                    plan.getPrice(), 
                    PaymentTransaction.TransactionType.SUBSCRIPTION_PAYMENT,
                    plan.getCurrency()
                );
                transaction.setDescription("Subscription to " + plan.getName());
                subscription.getPaymentTransactions().add(transaction);
            }

            subscription = subscriptionRepository.save(subscription);

            logger.info("Successfully subscribed user {} to plan {} with subscription ID {}", 
                userId, plan.getName(), subscription.getId());

            return subscription;

        } catch (Exception e) {
            logger.error("Error subscribing user {} to plan {}: {}", userId, planId, e.getMessage(), e);
            throw new RuntimeException("Failed to create subscription: " + e.getMessage(), e);
        }
    }

    /**
     * Change user's subscription plan.
     */
    public UserSubscription changePlan(String userId, UUID newPlanId) {
        logger.info("Changing plan for user {} to plan {}", userId, newPlanId);
        
        try {
            UserSubscription subscription = getUserSubscription(userId)
                .orElseThrow(() -> new IllegalArgumentException("No active subscription found for user: " + userId));

            SubscriptionPlan newPlan = planRepository.findByIdAndIsActiveTrue(newPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found or inactive: " + newPlanId));

            SubscriptionPlan oldPlan = subscription.getSubscriptionPlan();

            // If changing to the same plan, do nothing
            if (oldPlan.getId().equals(newPlanId)) {
                logger.info("User {} already on plan {}, no change needed", userId, newPlan.getName());
                return subscription;
            }

            // Update subscription
            subscription.setSubscriptionPlan(newPlan);

            // Handle Stripe subscription update
            if (!newPlan.isFreePlan() && subscription.getStripeSubscriptionId() != null) {
                stripePaymentService.updateStripeSubscription(subscription.getStripeSubscriptionId(), newPlan);
            } else if (!newPlan.isFreePlan() && subscription.getStripeSubscriptionId() == null) {
                // Creating paid subscription from free
                String stripeSubscriptionId = stripePaymentService.createStripeSubscription(
                    subscription.getStripeCustomerId(), newPlan);
                subscription.setStripeSubscriptionId(stripeSubscriptionId);
            } else if (newPlan.isFreePlan() && subscription.getStripeSubscriptionId() != null) {
                // Downgrading to free
                stripePaymentService.cancelStripeSubscription(subscription.getStripeSubscriptionId(), false);
                subscription.setStripeSubscriptionId(null);
            }

            // Reset usage for new plan
            subscription.resetMonthlyUsage();
            subscription.renewSubscription();

            // Create transaction record for plan change
            PaymentTransaction transaction = new PaymentTransaction(
                subscription,
                newPlan.getPrice(),
                PaymentTransaction.TransactionType.SUBSCRIPTION_PAYMENT,
                newPlan.getCurrency()
            );
            transaction.setDescription("Plan change from " + oldPlan.getName() + " to " + newPlan.getName());
            transactionRepository.save(transaction);

            subscription = subscriptionRepository.save(subscription);

            logger.info("Successfully changed plan for user {} from {} to {}", 
                userId, oldPlan.getName(), newPlan.getName());

            return subscription;

        } catch (Exception e) {
            logger.error("Error changing plan for user {} to plan {}: {}", userId, newPlanId, e.getMessage(), e);
            throw new RuntimeException("Failed to change subscription plan: " + e.getMessage(), e);
        }
    }

    /**
     * Cancel user's subscription.
     */
    public void cancelSubscription(String userId, boolean immediately) {
        logger.info("Canceling subscription for user {} (immediate: {})", userId, immediately);
        
        try {
            UserSubscription subscription = getUserSubscription(userId)
                .orElseThrow(() -> new IllegalArgumentException("No active subscription found for user: " + userId));

            // Cancel Stripe subscription if exists
            if (subscription.getStripeSubscriptionId() != null) {
                stripePaymentService.cancelStripeSubscription(subscription.getStripeSubscriptionId(), immediately);
            }

            // Update local subscription
            subscription.cancelSubscription();
            
            if (immediately) {
                // Immediately downgrade to free plan
                SubscriptionPlan freePlan = getFreePlan();
                subscription.setSubscriptionPlan(freePlan);
                subscription.setStatus(UserSubscription.SubscriptionStatus.CANCELED);
                subscription.setEndsAt(LocalDateTime.now());
            }

            subscriptionRepository.save(subscription);

            logger.info("Successfully canceled subscription for user {} (immediate: {})", userId, immediately);

        } catch (Exception e) {
            logger.error("Error canceling subscription for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to cancel subscription: " + e.getMessage(), e);
        }
    }

    /**
     * Check if user can access premium features.
     */
    public boolean canAccessPremiumFeatures(String userId) {
        Optional<UserSubscription> subscription = getUserSubscription(userId);
        
        return subscription.isPresent() && 
               subscription.get().isActive() && 
               subscription.get().getSubscriptionPlan().getIncludesPremiumFeatures();
    }

    /**
     * Check if user can make a scorecard request.
     */
    public boolean canMakeScorecardRequest(String userId) {
        Optional<UserSubscription> subscription = getUserSubscription(userId);
        
        return subscription.isPresent() && subscription.get().canMakeScorecardRequest();
    }

    /**
     * Track scorecard usage for user.
     */
    public void trackScorecardUsage(String userId) {
        logger.debug("Tracking scorecard usage for user: {}", userId);
        
        Optional<UserSubscription> subscriptionOpt = getUserSubscription(userId);
        if (subscriptionOpt.isPresent()) {
            UserSubscription subscription = subscriptionOpt.get();
            subscription.incrementScorecardUsage();
            subscriptionRepository.save(subscription);
        }
    }

    /**
     * Track API usage for user.
     */
    public void trackApiUsage(String userId) {
        logger.debug("Tracking API usage for user: {}", userId);
        
        Optional<UserSubscription> subscriptionOpt = getUserSubscription(userId);
        if (subscriptionOpt.isPresent()) {
            UserSubscription subscription = subscriptionOpt.get();
            subscription.incrementApiUsage();
            subscriptionRepository.save(subscription);
        }
    }

    /**
     * Get user's billing history.
     */
    public List<PaymentTransaction> getBillingHistory(String userId) {
        logger.debug("Getting billing history for user: {}", userId);
        
        Optional<UserSubscription> subscription = getUserSubscription(userId);
        if (subscription.isPresent()) {
            return transactionRepository.findByUserSubscriptionIdOrderByCreatedAtDesc(subscription.get().getId());
        }
        
        return List.of();
    }

    /**
     * Process subscription renewal.
     */
    public void renewSubscription(String userId) {
        logger.info("Renewing subscription for user: {}", userId);
        
        try {
            UserSubscription subscription = getUserSubscription(userId)
                .orElseThrow(() -> new IllegalArgumentException("No subscription found for user: " + userId));

            if (!subscription.getAutoRenew()) {
                logger.info("Auto-renew disabled for user {}, skipping renewal", userId);
                return;
            }

            // Renew the subscription
            subscription.renewSubscription();

            // Create transaction record for renewal
            PaymentTransaction transaction = new PaymentTransaction(
                subscription,
                subscription.getSubscriptionPlan().getPrice(),
                PaymentTransaction.TransactionType.SUBSCRIPTION_PAYMENT,
                subscription.getSubscriptionPlan().getCurrency()
            );
            transaction.setDescription("Subscription renewal - " + subscription.getSubscriptionPlan().getName());
            transaction.markAsSucceeded();
            
            transactionRepository.save(transaction);
            subscriptionRepository.save(subscription);

            logger.info("Successfully renewed subscription for user {}", userId);

        } catch (Exception e) {
            logger.error("Error renewing subscription for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to renew subscription: " + e.getMessage(), e);
        }
    }

    // Helper methods

    private UserSubscription createFreeSubscription(String userId) {
        SubscriptionPlan freePlan = getFreePlan();
        UserSubscription subscription = new UserSubscription(userId, freePlan);
        return subscriptionRepository.save(subscription);
    }

    private SubscriptionPlan getFreePlan() {
        return planRepository.findByPlanTypeAndIsActiveTrue(SubscriptionPlan.PlanType.FREE)
            .orElseThrow(() -> new RuntimeException("Free plan not found"));
    }

    private String getOrCreateStripeCustomer(String userId, String email, String name) {
        // In a real implementation, this would check if customer already exists
        // For now, always create a new one
        return stripePaymentService.createStripeCustomer(userId, email, name);
    }
}