package com.rushcrew.order_service.application.port.out;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 결제 서비스와의 동기 통신 Port (Feign Client)
 */
public interface PaymentPort {
	/**
	 * 결제 요청
	 * @param orderId 주문 ID
	 * @param userId 사용자 ID
	 * @param finalAmount 결제 금액
	 * @return 결제 성공 여부
	 */
	boolean requestPayment(UUID orderId, Long userId, BigDecimal finalAmount);

	/* 결제 취소 (환불) */
	void cancelPayment(UUID orderId, Long userId, BigDecimal finalAmount);
}
