package com.rushcrew.order_service.application.command.dto.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record RequestPaymentResult(
	UUID orderId,
	String orderStatus,
	BigDecimal paymentAmount,
	Instant paymentCompletedAt,
	Instant autoConfirmScheduledAt
) {}
