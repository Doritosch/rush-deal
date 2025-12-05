package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/*
 * 포인트 차감 완료 이벤트 (유저 -> 주문)
 */
public record PointDeductedEvent(
	Long userId,
	String orderId,
	String sagaId,
	BigDecimal deductedAmount,
	UUID pointHistoryId,
	BigDecimal newBalance,
	Instant timestamp
) {}
