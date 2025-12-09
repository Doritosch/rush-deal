package com.rushcrew.order_service.application.port.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record OutboxEvent(
	UUID eventId,
	String aggregateType,
	UUID aggregateId,
	String eventType,
	String payload,
	String status,
	Instant createdAt,
	Instant publishedAt,
	Instant failedAt,
	Integer retryCount,
	String errorMessage
) {}
