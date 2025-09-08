package com.buurtinzicht.domain.repository;

import com.buurtinzicht.domain.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    
    List<PaymentTransaction> findByUserSubscriptionIdOrderByCreatedAtDesc(UUID userSubscriptionId);
    
    Optional<PaymentTransaction> findByStripePaymentIntentId(String stripePaymentIntentId);
    
    Optional<PaymentTransaction> findByStripeChargeId(String stripeChargeId);
    
    List<PaymentTransaction> findByStatus(PaymentTransaction.TransactionStatus status);
    
    List<PaymentTransaction> findByTransactionTypeAndStatus(PaymentTransaction.TransactionType transactionType, PaymentTransaction.TransactionStatus status);
    
    @Query("SELECT t FROM PaymentTransaction t JOIN t.userSubscription s WHERE s.userId = :userId ORDER BY t.createdAt DESC")
    List<PaymentTransaction> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);
    
    @Query("SELECT t FROM PaymentTransaction t WHERE t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<PaymentTransaction> findTransactionsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.amount) FROM PaymentTransaction t WHERE t.status = 'SUCCEEDED' AND t.createdAt BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> getTotalRevenueBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM PaymentTransaction t WHERE t.status = 'SUCCEEDED' AND t.transactionType = 'SUBSCRIPTION_PAYMENT' AND t.createdAt BETWEEN :startDate AND :endDate")
    long countSuccessfulSubscriptionPayments(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM PaymentTransaction t WHERE t.status = 'FAILED' AND t.createdAt BETWEEN :startDate AND :endDate")
    long countFailedTransactions(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}