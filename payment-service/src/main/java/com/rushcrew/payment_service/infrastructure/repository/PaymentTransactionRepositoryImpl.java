package com.rushcrew.payment_service.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentTransactionRepositoryImpl {

    private final PaymentTransactionJpaRepository repository;

}