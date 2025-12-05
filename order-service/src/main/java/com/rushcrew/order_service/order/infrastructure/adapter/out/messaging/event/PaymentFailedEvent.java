package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
* 결제 실패 이벤트
* */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent {
	private String orderId;
	private UUID paymentId;
	private String sagaId;
	private String failureReason;
	private Instant timestamp;
}
