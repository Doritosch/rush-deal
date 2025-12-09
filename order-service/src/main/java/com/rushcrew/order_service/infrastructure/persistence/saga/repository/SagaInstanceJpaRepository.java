package com.rushcrew.order_service.infrastructure.persistence.saga.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;

public interface SagaInstanceJpaRepository extends JpaRepository<SagaInstance, UUID> {

	List<SagaInstance> findByStatusAndCreatedAtBefore(SagaStatus status, Instant createdAt);
}
