package com.rushcrew.order_service.application.port.out;

import java.util.UUID;

import lombok.NonNull;

public interface PointPort {
	/* 포인트 차감 */
	boolean deductPoint(@NonNull Long userId, @NonNull Long pointUsed, @NonNull UUID sagaId);

	/* 포인트 환불 */
	void refundPoint(@NonNull Long userId, @NonNull Long pointUsed, @NonNull UUID sagaId);
}
