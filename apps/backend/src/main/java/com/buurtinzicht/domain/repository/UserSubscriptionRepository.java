package com.buurtinzicht.domain.repository;

import com.buurtinzicht.domain.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID> {
    
    Optional<UserSubscription> findByUserIdAndStatus(String userId, UserSubscription.SubscriptionStatus status);
    
    List<UserSubscription> findByUserId(String userId);
    
    Optional<UserSubscription> findByStripeSubscriptionId(String stripeSubscriptionId);
    
    Optional<UserSubscription> findByStripeCustomerId(String stripeCustomerId);
    
    @Query("SELECT s FROM UserSubscription s WHERE s.userId = :userId AND s.status IN ('ACTIVE', 'TRIALING') ORDER BY s.createdAt DESC")
    Optional<UserSubscription> findActiveSubscriptionByUserId(@Param("userId") String userId);
    
    @Query("SELECT s FROM UserSubscription s WHERE s.status = 'ACTIVE' AND s.currentPeriodEnd < :now")
    List<UserSubscription> findExpiredSubscriptions(@Param("now") LocalDateTime now);
    
    @Query("SELECT s FROM UserSubscription s WHERE s.status = 'TRIALING' AND s.trialEnd < :now")
    List<UserSubscription> findExpiredTrials(@Param("now") LocalDateTime now);
    
    @Query("SELECT s FROM UserSubscription s WHERE s.autoRenew = true AND s.currentPeriodEnd BETWEEN :startDate AND :endDate")
    List<UserSubscription> findSubscriptionsForRenewal(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(s) FROM UserSubscription s WHERE s.subscriptionPlan.id = :planId AND s.status IN ('ACTIVE', 'TRIALING')")
    long countActiveSubscriptionsByPlan(@Param("planId") UUID planId);
    
    @Query("SELECT s FROM UserSubscription s WHERE s.subscriptionPlan.planType = :planType AND s.status IN ('ACTIVE', 'TRIALING')")
    List<UserSubscription> findActiveSubscriptionsByPlanType(@Param("planType") com.buurtinzicht.domain.model.SubscriptionPlan.PlanType planType);
}