package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.rushcrew.order_service.order.application.port.out.PaymentEventPort;
import com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event.PaymentRequestedEvent;
import com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event.PointDeductionRequestedEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher implements PaymentEventPort {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void publishPointDeductionRequested(
		@NonNull Long userId,
		@NonNull UUID orderId,
		@NonNull BigDecimal pointAmount,
		@NonNull String sagaId,
		@NonNull Instant timestamp
	) {
		PointDeductionRequestedEvent event = PointDeductionRequestedEvent.builder()
			.userId(userId)
			.orderId(orderId.toString())
			.pointAmount(pointAmount)
			.sagaId(sagaId)
			.timestamp(timestamp)
			.build();

		kafkaTemplate.send("point.deduction.requested", event);
	}

	@Override
	public void publishPaymentRequested(
		@NonNull UUID orderId,
		@NonNull Long userId,
		@NonNull BigDecimal originalAmount,
		@NonNull BigDecimal pointUsed,
		@NonNull BigDecimal finalAmount,
		@NonNull String sagaId,
		@NonNull Instant timestamp
	) {
		PaymentRequestedEvent event = PaymentRequestedEvent.builder()
			.orderId(orderId.toString())
			.userId(userId)
			.originalAmount(originalAmount)
			.pointUsed(pointUsed)
			.finalAmount(finalAmount)
			.sagaId(sagaId)
			.timestamp(timestamp)
			.build();

		kafkaTemplate.send("payment.requested", event);
	}
}
