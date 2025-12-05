package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
* 포인트 차감 요청 이벤트
* */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointDeductionRequestedEvent {
	private Long userId;
	private String orderId;
	private BigDecimal pointAmount;
	private String sagaId;
	private Instant timestamp;
}
