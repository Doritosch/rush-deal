package com.rushcrew.payment_service.infrastructure.repository;

import com.rushcrew.payment_service.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {

    @Query("SELECT * FROM Payment p WHERE p.orderId = :orderId")
    Optional<Payment> findByOrderId(UUID orderId);
}
