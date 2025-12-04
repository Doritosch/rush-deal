package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.rushcrew.order_service.order.application.port.out.PaymentEventPort;
import com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event.PaymentRequestedEvent;
import com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event.PointDeductionRequestedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher implements PaymentEventPort {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void publishPointDeductionRequested(
		Long userId,
		String orderId,
		BigDecimal pointAmount,
		String sagaId,
		Instant timestamp
	) {
		PointDeductionRequestedEvent event = PointDeductionRequestedEvent.builder()
			.userId(userId)
			.orderId(orderId)
			.pointAmount(pointAmount)
			.sagaId(sagaId)
			.timestamp(timestamp)
			.build();

		kafkaTemplate.send("point.deduction.requested", event);
	}

	@Override
	public void publishPaymentRequested(
		String orderId,
		Long userId,
		BigDecimal originalAmount,
		BigDecimal pointUsed,
		BigDecimal finalAmount,
		String paymentMethod,
		String sagaId,
		Instant timestamp
	) {
		PaymentRequestedEvent event = PaymentRequestedEvent.builder()
			.orderId(orderId)
			.userId(userId)
			.originalAmount(originalAmount)
			.pointUsed(pointUsed)
			.finalAmount(finalAmount)
			.paymentMethod(paymentMethod)
			.sagaId(sagaId)
			.timestamp(timestamp)
			.build();

		kafkaTemplate.send("payment.requested", event);
	}
}
