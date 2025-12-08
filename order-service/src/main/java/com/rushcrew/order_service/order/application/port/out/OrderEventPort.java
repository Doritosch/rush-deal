package com.rushcrew.order_service.order.application.port.out;

import java.time.Instant;
import java.util.UUID;

import lombok.NonNull;

public interface OrderEventPort {

	/**
	 * 재고 소진 이벤트 발행
	 * - 대기열 서비스가 수신하여 대기열 종료 처리
	 */
	void publishStockDepletedEvent(
		@NonNull UUID timeDealId,
		@NonNull Long userId,
		@NonNull Integer availableStock,
		@NonNull Instant timestamp);

}
