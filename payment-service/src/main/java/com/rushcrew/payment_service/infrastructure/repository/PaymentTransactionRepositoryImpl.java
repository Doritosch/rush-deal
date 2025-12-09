package com.rushcrew.payment_service.infrastructure.repository;

import com.rushcrew.payment_service.domain.model.PaymentTransaction;
import com.rushcrew.payment_service.domain.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentTransactionRepositoryImpl implements PaymentTransactionRepository {

    private final PaymentTransactionJpaRepository repository;

    @Override
    public PaymentTransaction save(PaymentTransaction paymentTransaction) {
        return repository.save(paymentTransaction);
    }
}