package com.rushcrew.payment_service.domain.repository;

import com.rushcrew.payment_service.domain.model.PaymentTransaction;

public interface PaymentTransactionRepository {
    PaymentTransaction save(PaymentTransaction paymentTransaction);
}