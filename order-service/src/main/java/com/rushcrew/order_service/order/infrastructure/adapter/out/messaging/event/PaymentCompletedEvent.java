package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.NonNull;

/*
* 결제 완료 이벤트
* */
public record PaymentCompletedEvent(
	@NonNull String orderId,
	@NonNull String paymentId,
	@NonNull BigDecimal capturedAmount,
	@NonNull String sagaId,
	@NonNull Instant timestamp
) {}
