package com.rushcrew.payment_service.infrastructure.repository;

import com.rushcrew.payment_service.domain.model.PaymentOutbox;
import com.rushcrew.payment_service.domain.vo.OutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentOutboxRepository extends JpaRepository<PaymentOutbox, UUID> {
    List<PaymentOutbox> findByStatusOrderByCreatedAtAsc(OutboxStatus status, Pageable pageable);
}
