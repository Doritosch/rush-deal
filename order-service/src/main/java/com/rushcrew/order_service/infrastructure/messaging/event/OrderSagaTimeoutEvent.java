package com.rushcrew.order_service.infrastructure.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record OrderSagaTimeoutEvent(
	UUID sagaId,
	Long userId,
	String sagaType,
	Instant occurredAt
) {}
