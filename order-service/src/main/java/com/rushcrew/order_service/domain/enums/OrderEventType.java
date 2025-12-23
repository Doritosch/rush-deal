package com.rushcrew.order_service.domain.enums;

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
	REFUNDED("결제 후 환불"), // PAID 상태에서만 가능, 구매확정 후에는 환불 불가
	POINT_USAGE_UPDATED("포인트 사용량 수정"),
	PAYMENT_FAILED("결제 실패");

	private final String description;
}
