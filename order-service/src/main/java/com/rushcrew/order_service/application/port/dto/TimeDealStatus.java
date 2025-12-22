package com.rushcrew.order_service.application.port.dto;

public enum TimeDealStatus {
	SCHEDULED,
	IN_PROGRESS,
	SOLD_OUT,
	ENDED;

	public static TimeDealStatus from(String status) {
		try {
			return TimeDealStatus.valueOf(status);
		} catch (Exception e) {
			// 알 수 없는 상태는 주문 불가로 처리
			return ENDED;
		}
	}

	public boolean isOrderable() {
		return this == IN_PROGRESS;
	}
}
