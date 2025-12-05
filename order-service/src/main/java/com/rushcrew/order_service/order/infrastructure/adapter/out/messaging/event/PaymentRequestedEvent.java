package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
* 결제 요청 이벤트
* */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestedEvent {
	private String orderId;
	private Long userId;
	private BigDecimal originalAmount;
	private BigDecimal pointUsed;
	private BigDecimal finalAmount;
	private String paymentMethod;
	private String sagaId;
	private Instant timestamp;
}
