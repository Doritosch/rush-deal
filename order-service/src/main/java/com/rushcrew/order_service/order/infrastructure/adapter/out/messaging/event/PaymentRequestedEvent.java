package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
* 결제 요청 이벤트
* */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestedEvent {
	@NonNull private String orderId;
	@NonNull private Long userId;
	@NonNull private BigDecimal originalAmount;
	@NonNull private BigDecimal pointUsed;
	@NonNull private BigDecimal finalAmount;
	@NonNull private String paymentMethod;
	@NonNull private String sagaId;
	@NonNull private Instant timestamp;
}
