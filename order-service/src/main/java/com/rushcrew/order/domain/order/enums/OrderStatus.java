package com.rushcrew.order.domain.order.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
	PENDING("결제 대기"),
	PAID("결제 완료"),
	PURCHASE_CONFIRMED("구매 확정"),
	CANCELLED("취소"),
	REFUNDED("환불");

	private final String description;

	OrderStatus(String description) {
		this.description = description;
	}
}
