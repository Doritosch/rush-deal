package com.rushcrew.order_service.order.application.port.out;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface PaymentEventPort {

	// 포인트 차감 요청 이벤트 발행
	void publishPointDeductionRequested(
		Long userId,
		UUID orderId,
		BigDecimal pointAmount,
		String sagaId,
		Instant timestamp
	);

	// 결제 요청 이벤트 발행
	void publishPaymentRequested(
		UUID orderId,
		Long userId,
		BigDecimal originalAmount,
		BigDecimal pointUsed,
		BigDecimal finalAmount,
		String paymentMethod,
		String sagaId,
		Instant timestamp
	);
}
