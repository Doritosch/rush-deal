package com.rushcrew.order_service.application.port.out;

import java.util.UUID;

public interface QueuePort {
	/* 대기열 토큰 검증 */
	boolean validateToken(UUID timeDealId, Long userId, String queueToken, String role);
	// boolean validateToken(@NonNull UUID timeDealId, @NonNull Long userId);
}
