package com.rushcrew.order_service.order.application.port.out;

import java.math.BigDecimal;
import java.time.Instant;

public interface PaymentEventPort {

	// 포인트 차감 요청 이벤트 발행
	void publishPointDeductionRequested(
		Long userId,
		String orderId,
		BigDecimal pointAmount,
		String sagaId,
		Instant timestamp
	);

	// 결제 요청 이벤트 발행
	void publishPaymentRequested(
		String orderId,
		Long userId,
		BigDecimal originalAmount,
		BigDecimal pointUsed,
		BigDecimal finalAmount,
		String paymentMethod,
		String sagaId,
		Instant timestamp
	);
}
