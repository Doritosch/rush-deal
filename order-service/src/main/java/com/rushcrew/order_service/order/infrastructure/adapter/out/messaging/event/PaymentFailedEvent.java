package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.time.Instant;
import java.util.UUID;

import lombok.NonNull;

/*
* 결제 실패 이벤트
* */
public record PaymentFailedEvent(
	@NonNull String orderId,
	@NonNull String paymentId,
	@NonNull String sagaId,
	@NonNull String failureReason,
	@NonNull Instant timestamp
) {}
