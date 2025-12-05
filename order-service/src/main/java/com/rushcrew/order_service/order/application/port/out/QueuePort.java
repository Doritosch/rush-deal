package com.rushcrew.order_service.order.application.port.out;

import java.util.UUID;

import lombok.NonNull;

public interface QueuePort {

	// 대기열 토큰 유효성 검증
	boolean validateQueueToken(
		@NonNull UUID timeDealId,
		@NonNull Long userId);

	// 대기열 토큰 TTL 연장
	void extendTokenTtl(
		@NonNull UUID timeDealId,
		@NonNull Long userId,
		@NonNull int seconds);

	// 사용자 대기열에서 제거
	void removeUserToken(
		@NonNull UUID timeDealId,
		@NonNull Long userId);
}
