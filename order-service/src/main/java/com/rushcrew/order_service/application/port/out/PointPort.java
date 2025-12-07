package com.rushcrew.order_service.application.port.out;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.NonNull;

public interface PointPort {
	/* 포인트 차감 */
	boolean deductPoint(
		@NonNull Long userId,
		@NonNull BigDecimal amount,
		@NonNull UUID sagaId,
		@NonNull String reason
	);

	/* 포인트 환불 */
	void refundPoint(
		@NonNull Long userId,
		@NonNull BigDecimal amount,
		@NonNull UUID sagaId,
		@NonNull String reason
	);
}
