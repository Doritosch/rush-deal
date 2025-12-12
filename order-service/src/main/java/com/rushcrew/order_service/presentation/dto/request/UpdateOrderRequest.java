package com.rushcrew.order_service.presentation.dto.request;

import java.math.BigDecimal;

import com.rushcrew.order_service.domain.vo.ShippingInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

public record UpdateOrderRequest(
	@Valid
	ShippingInfoRequest shippingInfo,  // null 가능 (수정하지 않을 경우)

	@Min(value = 0, message = "포인트 사용량은 0 이상이어야 합니다")
	BigDecimal pointUsed  // null 가능 (수정하지 않을 경우, PENDING 상태에서만 가능)
) {
	public record ShippingInfoRequest(
		String recipientName,
		String recipientPhone,
		String zipCode,
		String addressBase,
		String addressDetail,
		String deliveryMessage
	) {
		public ShippingInfo toShippingInfo() {
			return ShippingInfo.builder()
				.recipientName(recipientName)
				.recipientPhone(recipientPhone)
				.zipCode(zipCode)
				.addressBase(addressBase)
				.addressDetail(addressDetail)
				.deliveryMessage(deliveryMessage)
				.build();
		}
	}
}
