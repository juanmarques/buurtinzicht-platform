package com.buurtinzicht.domain.repository;

import com.buurtinzicht.domain.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {
    
    List<SubscriptionPlan> findByIsActiveTrueOrderBySortOrderAsc();
    
    Optional<SubscriptionPlan> findByIdAndIsActiveTrue(UUID id);
    
    Optional<SubscriptionPlan> findByPlanTypeAndIsActiveTrue(SubscriptionPlan.PlanType planType);
    
    List<SubscriptionPlan> findByPlanTypeAndIsActiveTrueOrderByPriceAsc(SubscriptionPlan.PlanType planType);
    
    @Query("SELECT p FROM SubscriptionPlan p WHERE p.isActive = true AND p.isFeatured = true ORDER BY p.sortOrder ASC")
    List<SubscriptionPlan> findFeaturedPlans();
    
    @Query("SELECT p FROM SubscriptionPlan p WHERE p.isActive = true AND p.planType IN :planTypes ORDER BY p.sortOrder ASC")
    List<SubscriptionPlan> findByPlanTypesAndIsActiveTrue(@Param("planTypes") List<SubscriptionPlan.PlanType> planTypes);
    
    Optional<SubscriptionPlan> findByStripePriceId(String stripePriceId);
    
    Optional<SubscriptionPlan> findByStripeProductId(String stripeProductId);
}