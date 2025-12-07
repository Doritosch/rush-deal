package com.rushcrew.order_service.infrastructure.persistence.outbox.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rushcrew.order_service.infrastructure.persistence.outbox.entity.OutboxEventEntity;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, UUID> {
}
