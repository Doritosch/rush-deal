package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;

/*
* 결제 요청 이벤트
* */
@Builder
public record PaymentRequestedEvent(
	@NonNull String orderId,
	@NonNull Long userId,
	@NonNull BigDecimal originalAmount,
	@NonNull BigDecimal pointUsed,
	@NonNull BigDecimal finalAmount,
	@NonNull String sagaId,
	@NonNull Instant timestamp
) {}
