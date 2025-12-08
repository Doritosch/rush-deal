package com.rushcrew.order_service.infrastructure.adapter.out.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.order_service.application.port.out.PaymentEventPort;
import com.rushcrew.order_service.infrastructure.persistence.outbox.entity.OutboxEventEntity;
import com.rushcrew.order_service.infrastructure.persistence.outbox.repository.OutboxEventJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymetEventPublisher implements PaymentEventPort {

	private final OutboxEventJpaRepository outboxRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void publishPaymentCompleted(UUID orderId, Long userId, BigDecimal finalAmount, Instant timestamp) {
		try {
			log.info("결제 완료 이벤트 발행: orderId={}, userId={}, finalAmount={}", orderId, userId, finalAmount);

			Map<String, Object> event = new HashMap<>();
			event.put("orderId", orderId.toString());
			event.put("userId", userId);
			event.put("amount", finalAmount);
			event.put("timestamp", timestamp.toString());

			String payload = objectMapper.writeValueAsString(event);

			OutboxEventEntity outbox = OutboxEventEntity.create(
				"ORDER",       // aggregateType
				orderId,                    // aggregateId
				"PAYMENT_COMPLETED",        // eventType
				payload                     // json
			);

			outboxRepository.save(outbox);
			log.info("결제 완료 이벤트 Outbox 저장 완료: orderId={}", orderId);

		} catch (Exception e) {
			log.error("결제 완료 이벤트 발행 실패: orderId={}", orderId, e);
			throw new RuntimeException("결제 완료 이벤트 발행 실패", e);
		}
	}
}
