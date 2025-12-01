package com.rushcrew.payment_service.infrastructure.repository;

import com.rushcrew.payment_service.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {
}
