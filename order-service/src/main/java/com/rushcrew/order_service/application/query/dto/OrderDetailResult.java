package com.rushcrew.order_service.application.query.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDetailResult(
	UUID orderId,
	Long userId,
	String orderStatus,
	BigDecimal totalAmount,
	BigDecimal finalAmount,
	Instant orderedAt,
	Instant reservationExpiresAt,
	List<OrderItemResult> orderItems
) {}

