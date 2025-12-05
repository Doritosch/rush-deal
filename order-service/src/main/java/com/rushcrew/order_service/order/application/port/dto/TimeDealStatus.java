package com.rushcrew.order_service.order.application.port.dto;

// 지금 TimeDealStatus 를 TimeDeal 상태랑 TimeDealStock 상태에 같이 쓰고 있음 추후 고민해봐야할 것 같음
public enum TimeDealStatus {
	SCHEDULED,
	IN_PROGRESS,
	SOLD_OUT,
	ENDED
}
