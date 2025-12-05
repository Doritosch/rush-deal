package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.rushcrew.order_service.order.application.port.out.PointEventPort;
import com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event.PointEarnRequestedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointEventPublisher implements PointEventPort {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void publishPointEarnRequested(Long userId, String orderId, BigDecimal earnAmount, String reason,
		Instant timestamp) {
		PointEarnRequestedEvent event = PointEarnRequestedEvent.builder()
			.userId(userId)
			.orderId(orderId)
			.earnAmount(earnAmount)
			.reason(reason)
			.timestamp(timestamp)
			.build();

		kafkaTemplate.send("point.earn.requested", event);
	}
}
