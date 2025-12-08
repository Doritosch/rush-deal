package com.rushcrew.order_service.infrastructure.adapter.out.client;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.port.out.PaymentPort;
import com.rushcrew.order_service.infrastructure.adapter.out.client.feign.PaymentFeignClient;
import com.rushcrew.order_service.infrastructure.dto.payment.PaymentRequest;
import com.rushcrew.order_service.infrastructure.dto.payment.PaymentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentAdapter implements PaymentPort {

	private final PaymentFeignClient paymentFeignClient;

	@Override
	public boolean requestPayment(UUID orderId, Long userId, BigDecimal finalAmount) {
		try {
			log.info("결제 요청: orderId={}, userId={}, finalAmount={}", orderId, userId, finalAmount);

			PaymentRequest request = PaymentRequest.builder()
				.orderId(orderId)
				.userId(userId)
				.finalAmount(finalAmount)
				.build();

			PaymentResponse response = paymentFeignClient.requestPayment(request);

			if (response.isSuccess()) {
				log.info("결제 요청 성공: orderId={}, paymentId={}", orderId, response.getPaymentId());
				return true;
			} else {
				log.warn("결제 요청 실패: orderId={}, message={}", orderId, response.getMessage());
				return false;
			}

		} catch (Exception e) {
			log.error("결제 요청 중 오류 발생: orderId={}, userId={}", orderId, userId, e);
			throw new RuntimeException("결제 요청 실패", e);
		}
	}

	@Override
	public void cancelPayment(UUID orderId, Long userId, BigDecimal finalAmount) {
		try {
			log.info("결제 취소 요청: orderId={}, userId={}, finalAmount={}", orderId, userId, finalAmount);

			PaymentRequest request = PaymentRequest.builder()
				.orderId(orderId)
				.userId(userId)
				.finalAmount(finalAmount)
				.build();

			paymentFeignClient.cancelPayment(request);

			log.info("결제 취소 성공: orderId={}", orderId);

		} catch (Exception e) {
			log.error("결제 취소 중 오류 발생: orderId={}, userId={}", orderId, userId, e);
			throw new RuntimeException("결제 취소 실패", e);
		}
	}
}
