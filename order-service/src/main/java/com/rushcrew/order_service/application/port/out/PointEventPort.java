package com.rushcrew.order_service.application.port.out;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface PointEventPort {
	/* 포인트 적립 요청 이벤트 발행 */
	void publishPointEarnRequested(Long userId, UUID orderId, BigDecimal finalAmount, String reason, Instant timestamp);
}
