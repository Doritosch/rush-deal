package com.rushcrew.order_service.infrastructure.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
	UUID orderId,
	Long userId,
	String status,
	BigDecimal totalAmount,
	Long pointUsed,
	BigDecimal finalAmount,
	Instant orderedAt
) {}
