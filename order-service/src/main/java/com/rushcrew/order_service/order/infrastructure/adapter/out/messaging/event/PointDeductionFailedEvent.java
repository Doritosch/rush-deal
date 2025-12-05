package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.NonNull;

/*
 * 포인트 차감 실패 이벤트 (유저 -> 주문)
 */
public record PointDeductionFailedEvent(
	@NonNull Long userId,
	@NonNull String orderId,
	@NonNull String sagaId,
	@NonNull String reason,
	@NonNull BigDecimal requiredAmount,   // 사용 요청한 포인트(필요한 포인트)
	@NonNull BigDecimal currentBalance,
	@NonNull Instant timestamp
) {}
