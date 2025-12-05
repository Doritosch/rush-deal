package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

/*
 * 포인트 차감 실패 이벤트 (유저 -> 주문)
 */
public record PointDeductionFailedEvent(
	Long userId,
	String orderId,
	String sagaId,
	String reason,
	BigDecimal requiredAmount,   // 사용 요청한 포인트(필요한 포인트)
	BigDecimal currentBalance,
	Instant timestamp
) {}
