package com.rushcrew.order_service.order.application.port.out;

public interface QueuePort {

	// 대기열 토큰 유효성 검증
	boolean validateQueueToken(String timeDealId, Long userId);

	// 대기열 토큰 TTL 연장
	void extendTokenTtl(String timeDealId, Long userId, int seconds);

	// 사용자 대기열에서 제거
	void removeUserToken(String timeDealId, Long userId);
}
