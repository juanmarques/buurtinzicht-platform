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
 * Entity representing subscription plans available in the Buurtinzicht platform.
 * Different plans provide different levels of access to neighborhood insights.
 */
@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Plan name is required")
    @Size(max = 100, message = "Plan name cannot exceed 100 characters")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Column(name = "plan_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Plan type is required")
    private PlanType planType;

    @Column(name = "billing_interval", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Billing interval is required")
    private BillingInterval billingInterval;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    private BigDecimal price;

    @Column(name = "currency", nullable = false, length = 3)
    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter code")
    private String currency = "EUR";

    @Column(name = "stripe_price_id", length = 100)
    @Size(max = 100, message = "Stripe price ID cannot exceed 100 characters")
    private String stripePriceId;

    @Column(name = "stripe_product_id", length = 100)
    @Size(max = 100, message = "Stripe product ID cannot exceed 100 characters")
    private String stripeProductId;

    // Feature Limits
    @Column(name = "max_scorecard_requests_per_month")
    @Min(value = 0, message = "Max scorecard requests must be non-negative")
    private Integer maxScorecardRequestsPerMonth;

    @Column(name = "max_api_calls_per_day")
    @Min(value = 0, message = "Max API calls must be non-negative")
    private Integer maxApiCallsPerDay;

    @Column(name = "includes_premium_features", nullable = false)
    private Boolean includesPremiumFeatures = false;

    @Column(name = "includes_historic_data", nullable = false)
    private Boolean includesHistoricData = false;

    @Column(name = "includes_comparisons", nullable = false)
    private Boolean includesComparisons = false;

    @Column(name = "includes_api_access", nullable = false)
    private Boolean includesApiAccess = false;

    @Column(name = "includes_bulk_exports", nullable = false)
    private Boolean includesBulkExports = false;

    @Column(name = "includes_priority_support", nullable = false)
    private Boolean includesPrioritySupport = false;

    @Column(name = "trial_period_days")
    @Min(value = 0, message = "Trial period must be non-negative")
    private Integer trialPeriodDays = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(name = "sort_order")
    @Min(value = 0, message = "Sort order must be non-negative")
    private Integer sortOrder = 0;

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
    @OneToMany(mappedBy = "subscriptionPlan", cascade = CascadeType.ALL)
    private Set<UserSubscription> subscriptions;

    // Enums
    public enum PlanType {
        FREE("Free plan with basic features"),
        B2C_BASIC("Basic plan for individual consumers"),
        B2C_PREMIUM("Premium plan for individual consumers"),
        B2B_STARTER("Starter plan for small businesses"),
        B2B_PROFESSIONAL("Professional plan for medium businesses"),
        B2B_ENTERPRISE("Enterprise plan for large organizations");

        private final String description;

        PlanType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum BillingInterval {
        MONTHLY("Monthly billing"),
        QUARTERLY("Quarterly billing"),
        YEARLY("Yearly billing"),
        ONE_TIME("One-time payment");

        private final String description;

        BillingInterval(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Constructors
    public SubscriptionPlan() {}

    public SubscriptionPlan(String name, PlanType planType, BigDecimal price, BillingInterval billingInterval) {
        this.name = name;
        this.planType = planType;
        this.price = price;
        this.billingInterval = billingInterval;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public BillingInterval getBillingInterval() {
        return billingInterval;
    }

    public void setBillingInterval(BillingInterval billingInterval) {
        this.billingInterval = billingInterval;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStripePriceId() {
        return stripePriceId;
    }

    public void setStripePriceId(String stripePriceId) {
        this.stripePriceId = stripePriceId;
    }

    public String getStripeProductId() {
        return stripeProductId;
    }

    public void setStripeProductId(String stripeProductId) {
        this.stripeProductId = stripeProductId;
    }

    public Integer getMaxScorecardRequestsPerMonth() {
        return maxScorecardRequestsPerMonth;
    }

    public void setMaxScorecardRequestsPerMonth(Integer maxScorecardRequestsPerMonth) {
        this.maxScorecardRequestsPerMonth = maxScorecardRequestsPerMonth;
    }

    public Integer getMaxApiCallsPerDay() {
        return maxApiCallsPerDay;
    }

    public void setMaxApiCallsPerDay(Integer maxApiCallsPerDay) {
        this.maxApiCallsPerDay = maxApiCallsPerDay;
    }

    public Boolean getIncludesPremiumFeatures() {
        return includesPremiumFeatures;
    }

    public void setIncludesPremiumFeatures(Boolean includesPremiumFeatures) {
        this.includesPremiumFeatures = includesPremiumFeatures;
    }

    public Boolean getIncludesHistoricData() {
        return includesHistoricData;
    }

    public void setIncludesHistoricData(Boolean includesHistoricData) {
        this.includesHistoricData = includesHistoricData;
    }

    public Boolean getIncludesComparisons() {
        return includesComparisons;
    }

    public void setIncludesComparisons(Boolean includesComparisons) {
        this.includesComparisons = includesComparisons;
    }

    public Boolean getIncludesApiAccess() {
        return includesApiAccess;
    }

    public void setIncludesApiAccess(Boolean includesApiAccess) {
        this.includesApiAccess = includesApiAccess;
    }

    public Boolean getIncludesBulkExports() {
        return includesBulkExports;
    }

    public void setIncludesBulkExports(Boolean includesBulkExports) {
        this.includesBulkExports = includesBulkExports;
    }

    public Boolean getIncludesPrioritySupport() {
        return includesPrioritySupport;
    }

    public void setIncludesPrioritySupport(Boolean includesPrioritySupport) {
        this.includesPrioritySupport = includesPrioritySupport;
    }

    public Integer getTrialPeriodDays() {
        return trialPeriodDays;
    }

    public void setTrialPeriodDays(Integer trialPeriodDays) {
        this.trialPeriodDays = trialPeriodDays;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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

    public Set<UserSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<UserSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    // Helper methods
    public boolean isFreePlan() {
        return planType == PlanType.FREE;
    }

    public boolean isB2CPlan() {
        return planType == PlanType.B2C_BASIC || planType == PlanType.B2C_PREMIUM;
    }

    public boolean isB2BPlan() {
        return planType == PlanType.B2B_STARTER || 
               planType == PlanType.B2B_PROFESSIONAL || 
               planType == PlanType.B2B_ENTERPRISE;
    }

    public boolean hasUnlimitedScorecards() {
        return maxScorecardRequestsPerMonth == null || maxScorecardRequestsPerMonth <= 0;
    }

    public boolean hasUnlimitedApiCalls() {
        return maxApiCallsPerDay == null || maxApiCallsPerDay <= 0;
    }

    public boolean hasTrialPeriod() {
        return trialPeriodDays != null && trialPeriodDays > 0;
    }

    public BigDecimal getMonthlyPrice() {
        return switch (billingInterval) {
            case MONTHLY -> price;
            case QUARTERLY -> price.multiply(BigDecimal.valueOf(3));
            case YEARLY -> price.multiply(BigDecimal.valueOf(12));
            case ONE_TIME -> price;
        };
    }

    public BigDecimal getYearlyPrice() {
        return switch (billingInterval) {
            case MONTHLY -> price.multiply(BigDecimal.valueOf(12));
            case QUARTERLY -> price.multiply(BigDecimal.valueOf(4));
            case YEARLY -> price;
            case ONE_TIME -> price;
        };
    }
}