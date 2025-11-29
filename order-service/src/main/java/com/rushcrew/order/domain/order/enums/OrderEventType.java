package com.rushcrew.order.domain.order.enums;

import lombok.Getter;

@Getter
public enum OrderEventType {
	ORDER_CREATED("주문 생성"),
	SHIPPING_INFO_UPDATED("배송지 정보 수정"),
	PAYMENT_COMPLETED("결제 완료"),
	PURCHASE_CONFIRMED("구매 확정"),
	CANCELLED_BEFORE_PAYMENT("결제 전 취소"),
	CANCELLED_AFTER_PAYMENT("결제 후 취소"),
	REFUNDED("구매확정 후 환불");

	private final String description;

	OrderEventType(String description) {
		this.description = description;
	}
}
