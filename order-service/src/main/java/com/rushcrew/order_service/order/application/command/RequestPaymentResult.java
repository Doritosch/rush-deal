package com.rushcrew.order_service.order.application.command;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RequestPaymentResult {
	private UUID orderId;
	private String orderStatus;
	private BigDecimal finalAmount;
	private BigDecimal pointUsed;
	private String paymentStatus;  // READY, IN_PROGRESS, DONE, EXPIRED, CANCELLED, ABORTED
	private String sagaId;
	private Instant requestedAt;
}
