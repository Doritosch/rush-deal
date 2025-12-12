package com.rushcrew.order_service.application.port.out;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 결제 관련 이벤트 발행 Port (Kafka 비동기 통신)
 */
public interface PaymentEventPort {
	/* 결제 완료 이벤트 발행 */
	void publishPaymentCompleted(UUID orderId, Long userId, BigDecimal finalAmount, Instant now);

	/* 환불 요청 이벤트 발행 */
	void publishRefundRequested(UUID orderId, Long userId, BigDecimal finalAmount, String s, Instant now);
}
