package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.time.Instant;
import java.util.UUID;

/*
* 결제 실패 이벤트
* */
public record PaymentFailedEvent(
	String orderId,
	UUID paymentId,
	String sagaId,
	String failurReason,
	Instant timestamp
) {}
