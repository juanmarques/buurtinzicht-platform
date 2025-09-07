package com.buurtinzicht.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing a user's subscription to a specific plan.
 */
@Entity
@Table(name = "user_subscriptions")
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID is required")
    private String userId; // Keycloak user ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    @NotNull(message = "Subscription plan is required")
    private SubscriptionPlan subscriptionPlan;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    private SubscriptionStatus status;

    @Column(name = "stripe_subscription_id", length = 100)
    private String stripeSubscriptionId;

    @Column(name = "stripe_customer_id", length = 100)
    private String stripeCustomerId;

    @Column(name = "current_period_start")
    private LocalDateTime currentPeriodStart;

    @Column(name = "current_period_end")
    private LocalDateTime currentPeriodEnd;

    @Column(name = "trial_start")
    private LocalDateTime trialStart;

    @Column(name = "trial_end")
    private LocalDateTime trialEnd;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew = true;

    // Usage tracking
    @Column(name = "scorecard_requests_this_month")
    @Min(value = 0, message = "Scorecard requests must be non-negative")
    private Integer scorecardRequestsThisMonth = 0;

    @Column(name = "api_calls_today")
    @Min(value = 0, message = "API calls must be non-negative")
    private Integer apiCallsToday = 0;

    @Column(name = "last_usage_reset")
    private LocalDateTime lastUsageReset;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "version", nullable = false)
    @Version
    private Long version = 0L;

    // Relationships
    @OneToMany(mappedBy = "userSubscription", cascade = CascadeType.ALL)
    private Set<PaymentTransaction> paymentTransactions;

    public enum SubscriptionStatus {
        ACTIVE("Active subscription"),
        TRIALING("In trial period"),
        PAST_DUE("Payment past due"),
        CANCELED("Canceled subscription"),
        UNPAID("Unpaid subscription"),
        INCOMPLETE("Incomplete subscription"),
        INCOMPLETE_EXPIRED("Incomplete subscription expired"),
        PAUSED("Paused subscription");

        private final String description;

        SubscriptionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Constructors
    public UserSubscription() {}

    public UserSubscription(String userId, SubscriptionPlan subscriptionPlan) {
        this.userId = userId;
        this.subscriptionPlan = subscriptionPlan;
        this.status = SubscriptionStatus.ACTIVE;
        this.currentPeriodStart = LocalDateTime.now();
        
        // Calculate period end based on billing interval
        if (subscriptionPlan.getBillingInterval() != null) {
            this.currentPeriodEnd = calculatePeriodEnd(subscriptionPlan.getBillingInterval());
        }
        
        // Set up trial if applicable
        if (subscriptionPlan.hasTrialPeriod()) {
            this.status = SubscriptionStatus.TRIALING;
            this.trialStart = LocalDateTime.now();
            this.trialEnd = this.trialStart.plusDays(subscriptionPlan.getTrialPeriodDays());
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public String getStripeSubscriptionId() {
        return stripeSubscriptionId;
    }

    public void setStripeSubscriptionId(String stripeSubscriptionId) {
        this.stripeSubscriptionId = stripeSubscriptionId;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public LocalDateTime getCurrentPeriodStart() {
        return currentPeriodStart;
    }

    public void setCurrentPeriodStart(LocalDateTime currentPeriodStart) {
        this.currentPeriodStart = currentPeriodStart;
    }

    public LocalDateTime getCurrentPeriodEnd() {
        return currentPeriodEnd;
    }

    public void setCurrentPeriodEnd(LocalDateTime currentPeriodEnd) {
        this.currentPeriodEnd = currentPeriodEnd;
    }

    public LocalDateTime getTrialStart() {
        return trialStart;
    }

    public void setTrialStart(LocalDateTime trialStart) {
        this.trialStart = trialStart;
    }

    public LocalDateTime getTrialEnd() {
        return trialEnd;
    }

    public void setTrialEnd(LocalDateTime trialEnd) {
        this.trialEnd = trialEnd;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public Boolean getAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public Integer getScorecardRequestsThisMonth() {
        return scorecardRequestsThisMonth;
    }

    public void setScorecardRequestsThisMonth(Integer scorecardRequestsThisMonth) {
        this.scorecardRequestsThisMonth = scorecardRequestsThisMonth;
    }

    public Integer getApiCallsToday() {
        return apiCallsToday;
    }

    public void setApiCallsToday(Integer apiCallsToday) {
        this.apiCallsToday = apiCallsToday;
    }

    public LocalDateTime getLastUsageReset() {
        return lastUsageReset;
    }

    public void setLastUsageReset(LocalDateTime lastUsageReset) {
        this.lastUsageReset = lastUsageReset;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Set<PaymentTransaction> getPaymentTransactions() {
        return paymentTransactions;
    }

    public void setPaymentTransactions(Set<PaymentTransaction> paymentTransactions) {
        this.paymentTransactions = paymentTransactions;
    }

    // Helper methods
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE || status == SubscriptionStatus.TRIALING;
    }

    public boolean isInTrial() {
        return status == SubscriptionStatus.TRIALING && 
               trialEnd != null && 
               LocalDateTime.now().isBefore(trialEnd);
    }

    public boolean isExpired() {
        return currentPeriodEnd != null && LocalDateTime.now().isAfter(currentPeriodEnd);
    }

    public boolean canMakeScorecardRequest() {
        if (!isActive()) return false;
        
        Integer maxRequests = subscriptionPlan.getMaxScorecardRequestsPerMonth();
        return maxRequests == null || 
               maxRequests <= 0 || 
               (scorecardRequestsThisMonth != null && scorecardRequestsThisMonth < maxRequests);
    }

    public boolean canMakeApiCall() {
        if (!isActive()) return false;
        
        Integer maxCalls = subscriptionPlan.getMaxApiCallsPerDay();
        return maxCalls == null || 
               maxCalls <= 0 || 
               (apiCallsToday != null && apiCallsToday < maxCalls);
    }

    public int getRemainingScorecardsThisMonth() {
        Integer maxRequests = subscriptionPlan.getMaxScorecardRequestsPerMonth();
        if (maxRequests == null || maxRequests <= 0) return Integer.MAX_VALUE;
        
        return Math.max(0, maxRequests - (scorecardRequestsThisMonth != null ? scorecardRequestsThisMonth : 0));
    }

    public int getRemainingApiCallsToday() {
        Integer maxCalls = subscriptionPlan.getMaxApiCallsPerDay();
        if (maxCalls == null || maxCalls <= 0) return Integer.MAX_VALUE;
        
        return Math.max(0, maxCalls - (apiCallsToday != null ? apiCallsToday : 0));
    }

    public void incrementScorecardUsage() {
        if (scorecardRequestsThisMonth == null) {
            scorecardRequestsThisMonth = 0;
        }
        scorecardRequestsThisMonth++;
    }

    public void incrementApiUsage() {
        if (apiCallsToday == null) {
            apiCallsToday = 0;
        }
        apiCallsToday++;
    }

    public void resetMonthlyUsage() {
        scorecardRequestsThisMonth = 0;
        lastUsageReset = LocalDateTime.now();
    }

    public void resetDailyUsage() {
        apiCallsToday = 0;
        lastUsageReset = LocalDateTime.now();
    }

    public void cancelSubscription() {
        status = SubscriptionStatus.CANCELED;
        canceledAt = LocalDateTime.now();
        autoRenew = false;
        
        // Set end date to end of current period
        if (currentPeriodEnd != null) {
            endsAt = currentPeriodEnd;
        } else {
            endsAt = LocalDateTime.now();
        }
    }

    public void renewSubscription() {
        if (subscriptionPlan.getBillingInterval() != null) {
            currentPeriodStart = LocalDateTime.now();
            currentPeriodEnd = calculatePeriodEnd(subscriptionPlan.getBillingInterval());
            status = SubscriptionStatus.ACTIVE;
            resetMonthlyUsage();
        }
    }

    private LocalDateTime calculatePeriodEnd(SubscriptionPlan.BillingInterval interval) {
        LocalDateTime start = LocalDateTime.now();
        return switch (interval) {
            case MONTHLY -> start.plusMonths(1);
            case QUARTERLY -> start.plusMonths(3);
            case YEARLY -> start.plusYears(1);
            case ONE_TIME -> start.plusYears(100); // Effectively never expires
        };
    }
}