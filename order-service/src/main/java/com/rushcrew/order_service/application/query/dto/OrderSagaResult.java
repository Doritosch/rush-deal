package com.rushcrew.order_service.application.query.dto;

import java.time.Instant;
import java.util.UUID;

import com.rushcrew.order_service.domain.enums.SagaStatus;

import lombok.Builder;

@Builder
public record OrderSagaResult(
	UUID sagaId,
	SagaStatus status,
	UUID orderId,
	String errorMessage,
	Instant createdAt,
	Instant completedAt,
	Instant failedAt
) {}
