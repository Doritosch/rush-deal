package com.rushcrew.payment_service.infrastructure.repository;

import com.rushcrew.payment_service.domain.model.Payment;
import com.rushcrew.payment_service.domain.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentTransactionJpaRepository extends JpaRepository<PaymentTransaction, UUID> {
    Optional<PaymentTransaction> findFirstByPaymentOrderByRequestedAtDesc(Payment payment);
}
