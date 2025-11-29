package com.rushcrew.order.domain.order.enums;

import lombok.Getter;

@Getter
public enum ReservationStatus {
	RESERVED("예약됨"),
	CONFIRMED("확정됨"),
	EXPIRED("만료됨"),
	CANCELLED("취소됨");

	private final String description;

	ReservationStatus(String description) {
		this.description = description;
	}
}
