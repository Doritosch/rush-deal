package com.rushcrew.order_service.application.port.out;

import java.util.UUID;

import lombok.NonNull;

public interface QueuePort {
	/* 대기열 토큰 검증 */
	boolean validateToken(@NonNull UUID timeDealId, @NonNull Long userId);
}
