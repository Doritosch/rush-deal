package com.rushcrew.order_service.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

public record UpdateOrderRequest(
	@Valid
	ShippingInfoRequest shippingInfo,  // null 가능

	@Min(value = 0, message = "포인트 사용량은 0 이상이어야 합니다")
	Long pointUsed
) {
	public record ShippingInfoRequest(
		String recipientName,
		String recipientPhone,
		String zipCode,
		String addressBase,
		String addressDetail,
		String deliveryMessage
	) {}
}
