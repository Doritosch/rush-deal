package com.rushcrew.order_service.order.application.port.out;

import java.util.UUID;

public interface QueuePort {

	// 대기열 토큰 유효성 검증
	boolean validateQueueToken(UUID timeDealId, Long userId);

	// 대기열 토큰 TTL 연장
	void extendTokenTtl(UUID timeDealId, Long userId, int seconds);

	// 사용자 대기열에서 제거
	void removeUserToken(UUID timeDealId, Long userId);
}
