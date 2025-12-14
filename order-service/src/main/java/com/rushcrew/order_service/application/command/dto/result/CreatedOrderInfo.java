package com.rushcrew.order_service.application.command.dto.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.rushcrew.order_service.application.query.dto.OrderItemQueryDto;

public record CreatedOrderInfo(
	UUID orderId,
	Long userId,
	String orderStatus,
	BigDecimal totalAmount,
	Long pointUsed,
	BigDecimal finalAmount,
	Instant orderedAt,
	List<OrderItemQueryDto> orderItems
) {}
