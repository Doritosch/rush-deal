package com.rushcrew.order_service.domain.enums;

public enum SagaStepName {

	// Order Creation Saga Steps
	CREATE_ORDER("주문 생성"),
	VALIDATE_STOCK("재고 검증"),
	USE_POINT("포인트 사용"),
	USE_POINT_COMPENSATE("포인트 사용 보상"),
	REQUEST_STOCK_RESERVATION("재고 예약 요청"),

	// Order Cancellation Saga Steps
	CANCEL_ORDER("주문 취소"),
	REFUND_POINT("포인트 환불"),
	RELEASE_STOCK("재고 해제");

	private final String description;

	SagaStepName(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
