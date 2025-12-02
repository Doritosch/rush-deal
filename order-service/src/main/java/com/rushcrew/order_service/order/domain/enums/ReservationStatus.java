package com.rushcrew.order_service.order.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
	RESERVED("예약됨"),
	CONFIRMED("확정됨"),
	EXPIRED("만료됨"),
	CANCELLED("취소됨");

	private final String description;
}
