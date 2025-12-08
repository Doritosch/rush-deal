package com.rushcrew.order_service.application.port.out;

import java.time.Instant;
import java.util.UUID;

public interface StockEventPort {
	/* 재고 예약 취소 이벤트 발행 */
	void publishStockReservationCancelled(UUID orderId, UUID timeDealStockId, Integer quantity, String reason, Instant timestamp);
}
