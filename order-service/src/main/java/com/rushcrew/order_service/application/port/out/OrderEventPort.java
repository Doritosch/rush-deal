package com.rushcrew.order_service.application.port.out;

import java.time.Instant;
import java.util.UUID;

import lombok.NonNull;

public interface OrderEventPort {
	/* 재고 소진 이벤트 발행 */
	void publishStockDepletedEvent(
		@NonNull UUID timeDealId,
		@NonNull Long userId,
		@NonNull Integer availableStock,
		@NonNull Instant timestamp
	);
}
