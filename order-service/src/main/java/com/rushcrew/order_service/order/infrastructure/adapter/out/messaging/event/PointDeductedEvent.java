package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
* 포인트 차감 완료 이벤트 (유저 -> 주문)
* */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointDeductedEvent {
	private Long userId;
	private String orderId;
	private String sagaId;
	private BigDecimal deductedAmount;
	private UUID pointHistoryId;
	private BigDecimal newBalance;
	private Instant timestamp;
}
