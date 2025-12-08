package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record PointEarnRequestedEvent(
	@NonNull Long userId,
	@NonNull String orderId,
	@NonNull BigDecimal earnAmount,
	@NonNull String reason,
	@NonNull Instant timestamp
) {}
