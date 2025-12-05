package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
* 포인트 차감 요청 이벤트
* */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointDeductionRequestedEvent {
	@NonNull private Long userId;
	@NonNull private String orderId;
	@NonNull private BigDecimal pointAmount;
	@NonNull private String sagaId;
	@NonNull private Instant timestamp;
}
