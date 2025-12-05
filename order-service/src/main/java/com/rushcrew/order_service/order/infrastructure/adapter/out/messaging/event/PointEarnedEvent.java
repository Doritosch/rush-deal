package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

/*
* 포인트 적립 완료 이벤트 유저 -> 주문
* */
public record PointEarnedEvent(
	Long userId,
	String orderId,
	BigDecimal earnedAmount,
	BigDecimal newBalance,
	// UUID pointHistoryId,
	Instant timestamp
) { }
