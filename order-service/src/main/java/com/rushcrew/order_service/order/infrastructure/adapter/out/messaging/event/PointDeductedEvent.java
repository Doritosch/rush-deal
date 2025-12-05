package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.NonNull;

/*
 * 포인트 차감 완료 이벤트 (유저 -> 주문)
 */
public record PointDeductedEvent(
	@NonNull Long userId,
	@NonNull String orderId,
	@NonNull String sagaId,
	@NonNull BigDecimal deductedAmount,
	@NonNull String pointHistoryId,
	@NonNull BigDecimal newBalance,
	@NonNull Instant timestamp
) {}
