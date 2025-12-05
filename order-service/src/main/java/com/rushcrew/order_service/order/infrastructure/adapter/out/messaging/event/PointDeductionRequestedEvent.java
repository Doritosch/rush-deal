package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;

/*
* 포인트 차감 요청 이벤트
* */
@Builder
public record PointDeductionRequestedEvent(
	@NonNull Long userId,
	@NonNull String orderId,
	@NonNull BigDecimal pointAmount,
	@NonNull String sagaId,
	@NonNull Instant timestamp
) {}
