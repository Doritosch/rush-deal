package com.rushcrew.order_service.presentation.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.rushcrew.order_service.application.command.dto.result.UpdateOrderResult;

public record UpdateOrderResponse(
	UUID orderId,
	String orderStatus,
	ShippingInfoResponse shippingInfo,
	BigDecimal pointUsed,
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

	public static UpdateOrderResponse from(UpdateOrderResult result) {
		return new UpdateOrderResponse(
			result.orderId(),
			result.orderStatus(),
			result.shippingInfo() != null ?
				new ShippingInfoResponse(
					result.shippingInfo().recipientName(),
					result.shippingInfo().recipientPhone(),
					result.shippingInfo().zipCode(),
					result.shippingInfo().addressBase(),
					result.shippingInfo().addressDetail(),
					result.shippingInfo().deliveryMessage()
				) : null,
			result.pointUsed(),
			result.totalAmount(),
			result.finalAmount(),
			result.updatedAt(),
			"주문이 수정되었습니다."
		);
	}
}
