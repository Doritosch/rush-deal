package com.rushcrew.payment_service.domain.repository;

import com.rushcrew.payment_service.domain.model.Payment;
import com.rushcrew.payment_service.domain.model.PaymentTransaction;

import java.util.Optional;

public interface PaymentTransactionRepository {
    PaymentTransaction save(PaymentTransaction paymentTransaction);
    Optional<PaymentTransaction> findFirstByPaymentOrderByRequestedAtDesc(Payment payment);
}