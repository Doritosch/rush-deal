package com.rushcrew.order_service.presentation.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RequestPaymentResponse {
	private UUID orderId;
	private String orderStatus;
	private BigDecimal paymentAmount;
	private Instant paymentCompletedAt;
	private Instant autoConfirmScheduledAt;
	private String message;
}
