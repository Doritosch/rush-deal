package com.rushcrew.order_service.order.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderEventType {
	ORDER_CREATED("주문 생성"),
	SHIPPING_INFO_UPDATED("배송지 정보 수정"),
	PAYMENT_COMPLETED("결제 완료"),
	PURCHASE_CONFIRMED("구매 확정"),
	CANCELLED_BEFORE_PAYMENT("결제 전 취소"),
	CANCELLED_AFTER_PAYMENT("결제 후 취소"),
	REFUNDED("구매확정 후 환불"),
	POINT_USAGE_UPDATED("포인트 사용량 수정"),
	POINT_DEDUCTION_FAILED("포인트 차감 실"),
	PAYMENT_FAILED("결제 실패");

	private final String description;
}
