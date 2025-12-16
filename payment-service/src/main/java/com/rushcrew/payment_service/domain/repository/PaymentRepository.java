package com.rushcrew.payment_service.domain.repository;

import com.rushcrew.payment_service.domain.model.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);
    Optional<Payment> findById(UUID paymentId);
    Optional<Payment> findByOrderId(UUID orderId);
    Optional<Payment> findByPortonePaymentId(String portonePaymentId);
}
