package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.rushcrew.order_service.order.application.port.out.PaymentEventPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher implements PaymentEventPort {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void publishPointDeductionRequested(Long userId, String orderId, BigDecimal pointAmount, String sagaId,
		Instant timestamp) {

	}

	@Override
	public void publishPaymentRequested(String orderId, Long userId, BigDecimal originalAmount, BigDecimal pointUsed,
		BigDecimal finalAmount, String paymentMethod, String sagaId, Instant timestamp) {

	}
}
