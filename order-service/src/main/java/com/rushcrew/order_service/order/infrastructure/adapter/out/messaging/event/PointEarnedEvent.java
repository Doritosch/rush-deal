package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.NonNull;

/*
* 포인트 적립 완료 이벤트 유저 -> 주문
* */
public record PointEarnedEvent(
	@NonNull Long userId,
	@NonNull String orderId,
	@NonNull BigDecimal earnedAmount,
	@NonNull BigDecimal newBalance,
	// @NonNull String pointHistoryId,
	@NonNull Instant timestamp
) { }
