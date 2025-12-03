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
public class PointEarnedEvent {
	private Long userId;
	private String orderId;
	private BigDecimal earnedAmount;
	// private UUID pointHistoryId;
	private BigDecimal newBalance;
	private Instant timestamp;
}
