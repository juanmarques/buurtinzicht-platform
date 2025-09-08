package com.buurtinzicht.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing payment transactions processed through the platform.
 */
@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_subscription_id", nullable = false)
    @NotNull(message = "User subscription is required")
    private UserSubscription userSubscription;

    @Column(name = "stripe_payment_intent_id", length = 100)
    private String stripePaymentIntentId;

    @Column(name = "stripe_charge_id", length = 100)
    private String stripeChargeId;

    @Column(name = "transaction_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    private TransactionStatus status;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00", message = "Amount must be non-negative")
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter code")
    private String currency = "EUR";

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "refunded_amount", precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Refunded amount must be non-negative")
    private BigDecimal refundedAmount;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "version", nullable = false)
    @Version
    private Long version = 0L;

    public enum TransactionType {
        SUBSCRIPTION_PAYMENT("Subscription payment"),
        SUBSCRIPTION_REFUND("Subscription refund"),
        ONE_TIME_PAYMENT("One-time payment"),
        SETUP_FEE("Setup fee"),
        CREDIT("Credit/adjustment"),
        CHARGEBACK("Chargeback");

        private final String description;

        TransactionType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum TransactionStatus {
        PENDING("Payment pending"),
        PROCESSING("Payment processing"),
        SUCCEEDED("Payment succeeded"),
        FAILED("Payment failed"),
        CANCELED("Payment canceled"),
        REFUNDED("Payment refunded"),
        PARTIALLY_REFUNDED("Payment partially refunded"),
        DISPUTED("Payment disputed");

        private final String description;

        TransactionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Constructors
    public PaymentTransaction() {}

    public PaymentTransaction(UserSubscription userSubscription, BigDecimal amount, 
                            TransactionType transactionType, String currency) {
        this.userSubscription = userSubscription;
        this.amount = amount;
        this.transactionType = transactionType;
        this.currency = currency;
        this.status = TransactionStatus.PENDING;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserSubscription getUserSubscription() {
        return userSubscription;
    }

    public void setUserSubscription(UserSubscription userSubscription) {
        this.userSubscription = userSubscription;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public String getStripeChargeId() {
        return stripeChargeId;
    }

    public void setStripeChargeId(String stripeChargeId) {
        this.stripeChargeId = stripeChargeId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(BigDecimal refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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

    // Helper methods
    public boolean isSuccessful() {
        return status == TransactionStatus.SUCCEEDED;
    }

    public boolean isFailed() {
        return status == TransactionStatus.FAILED || status == TransactionStatus.CANCELED;
    }

    public boolean isPending() {
        return status == TransactionStatus.PENDING || status == TransactionStatus.PROCESSING;
    }

    public boolean isRefunded() {
        return status == TransactionStatus.REFUNDED || status == TransactionStatus.PARTIALLY_REFUNDED;
    }

    public boolean canBeRefunded() {
        return isSuccessful() && !isRefunded();
    }

    public BigDecimal getRefundableAmount() {
        if (!canBeRefunded()) return BigDecimal.ZERO;
        
        if (refundedAmount == null) return amount;
        
        return amount.subtract(refundedAmount);
    }

    public void markAsSucceeded() {
        status = TransactionStatus.SUCCEEDED;
        processedAt = LocalDateTime.now();
    }

    public void markAsFailed(String reason) {
        status = TransactionStatus.FAILED;
        failureReason = reason;
        processedAt = LocalDateTime.now();
    }

    public void markAsRefunded(BigDecimal refundAmount) {
        refundedAmount = refundAmount;
        refundedAt = LocalDateTime.now();
        
        if (refundAmount.compareTo(amount) >= 0) {
            status = TransactionStatus.REFUNDED;
        } else {
            status = TransactionStatus.PARTIALLY_REFUNDED;
        }
    }
}