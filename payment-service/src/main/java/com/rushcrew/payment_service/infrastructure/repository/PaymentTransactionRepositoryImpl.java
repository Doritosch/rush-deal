package com.rushcrew.payment_service.infrastructure.repository;

import com.rushcrew.payment_service.domain.model.Payment;
import com.rushcrew.payment_service.domain.model.PaymentTransaction;
import com.rushcrew.payment_service.domain.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentTransactionRepositoryImpl implements PaymentTransactionRepository {

    private final PaymentTransactionJpaRepository repository;

    @Override
    public PaymentTransaction save(PaymentTransaction paymentTransaction) {
        return repository.save(paymentTransaction);
    }

    @Override
    public Optional<PaymentTransaction> findFirstByPaymentOrderByRequestedAtDesc(Payment payment) {
        return repository.findFirstByPaymentOrderByRequestedAtDesc(payment);
    }
}