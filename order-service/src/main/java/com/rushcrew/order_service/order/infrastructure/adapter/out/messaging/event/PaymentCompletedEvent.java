package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/*
* 결제 완료 이벤트
* */
public record PaymentCompletedEvent(
	String orderId,
	UUID paymentId,
	BigDecimal capturedAmount,
	String sagaId,
	Instant timestamp
) {}
