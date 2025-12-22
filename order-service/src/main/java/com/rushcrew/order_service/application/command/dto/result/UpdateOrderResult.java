package com.rushcrew.order_service.application.command.dto.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record UpdateOrderResult(
	UUID orderId,
	String orderStatus,
	ShippingInfoResult shippingInfo,
	Long pointUsed,
	BigDecimal totalAmount,
	BigDecimal finalAmount,
	Instant updatedAt
) {
	@Builder
	public record ShippingInfoResult(
		String recipientName,
		String recipientPhone,
		String zipCode,
		String addressBase,
		String addressDetail,
		String deliveryMessage
	) {}
}
