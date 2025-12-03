package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
* 포인트 차감 실패 이벤트 (유저 -> 주문)
* */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointDeductionFailedEvent {
	private Long userId;
	private String orderId;
	private String sagaId;
	private String reason;
	private BigDecimal requiredAmount;	// 사용 요청한 포인트(필요한 포인트)
	private BigDecimal currentBalance;
	private Instant timestamp;
}
