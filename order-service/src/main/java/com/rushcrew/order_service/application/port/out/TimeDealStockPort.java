package com.rushcrew.order_service.application.port.out;

import java.util.UUID;

import com.rushcrew.order_service.application.port.dto.TimeDealInfo;

import lombok.NonNull;

public interface TimeDealStockPort {
	// 타임딜 정보 조회
	TimeDealInfo getTimeDeal(@NonNull UUID uuid);
}
