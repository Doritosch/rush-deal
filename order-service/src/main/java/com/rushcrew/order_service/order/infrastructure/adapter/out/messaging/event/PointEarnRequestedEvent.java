package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointEarnRequestedEvent {
	private Long userId;
	private String orderId;
	private BigDecimal earnAmount;
	private String reason;	// 구매확정, 자동 구매확정
	private Instant timestamp;
}
