package com.rushcrew.order_service.presentation.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UpdateOrderResponse(
	UUID orderId,
	String orderStatus,
	ShippingInfoResponse shippingInfo,
	Long pointUsed,
	BigDecimal totalAmount,
	BigDecimal finalAmount,
	Instant updatedAt,
	String message
) {
	public record ShippingInfoResponse(
		String recipientName,
		String recipientPhone,
		String zipCode,
		String addressBase,
		String addressDetail,
		String deliveryMessage
	) {}
}
