package com.rushcrew.order_service.order.application.port.out;

import java.math.BigDecimal;
import java.time.Instant;

public interface PointEventPort {
	// 포인트 적립 요청 이벤트 발행 - 구매확정 시 호출
	void publishPointEarnRequested(
		Long userId,
		String orderId,
		BigDecimal earnAmount,
		String reason,
		Instant timestamp
	);
}
