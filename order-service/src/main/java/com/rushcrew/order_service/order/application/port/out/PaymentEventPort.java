package com.rushcrew.order_service.order.application.port.out;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.NonNull;

public interface PaymentEventPort {

	// 포인트 차감 요청 이벤트 발행
	void publishPointDeductionRequested(
		@NonNull Long userId,
		@NonNull UUID orderId,
		@NonNull BigDecimal pointAmount,
		@NonNull String sagaId,
		@NonNull Instant timestamp
	);

	// 결제 요청 이벤트 발행
	void publishPaymentRequested(
		@NonNull UUID orderId,
		@NonNull Long userId,
		@NonNull BigDecimal originalAmount,
		@NonNull BigDecimal pointUsed,
		@NonNull BigDecimal finalAmount,
		@NonNull String sagaId,
		@NonNull Instant timestamp
	);
}
