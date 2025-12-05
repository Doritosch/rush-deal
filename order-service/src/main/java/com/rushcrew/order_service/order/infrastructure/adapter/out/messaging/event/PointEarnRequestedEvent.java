package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointEarnRequestedEvent {
	@NonNull private Long userId;
	@NonNull private String orderId;
	@NonNull private BigDecimal earnAmount;
	@NonNull private String reason;	// 구매확정, 자동 구매확정
	@NonNull private Instant timestamp;
}
