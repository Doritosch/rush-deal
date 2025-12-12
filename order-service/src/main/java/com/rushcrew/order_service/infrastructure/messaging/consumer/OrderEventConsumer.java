package com.rushcrew.order_service.infrastructure.messaging.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.order_service.application.command.port.out.OrderCachePort;
import com.rushcrew.order_service.application.query.dto.OrderDetailDto;
import com.rushcrew.order_service.infrastructure.messaging.event.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

	private final OrderCachePort orderCachePort;
	private final ObjectMapper objectMapper;

	/**
	 * ORDER_CREATED 이벤트 수신 → Redis 동기화
	 */
	@KafkaListener(topics = "order.created", groupId = "order-cache-sync")
	@Transactional
	public void handleOrderCreatedEvent(String message) {
		try {
			log.info("ORDER_CREATED 이벤트 수신: {}", message);

			OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);

			// Redis 캐시 업데이트
			OrderDetailDto dto = OrderDetailDto.builder()
				.orderId(event.orderId())
				.userId(event.userId())
				.orderStatus(event.status())
				.totalAmount(event.totalAmount())
				.pointUsed(event.pointUsed())
				.finalAmount(event.finalAmount())
				.orderedAt(event.orderedAt())
				.build();

			orderCachePort.updateOrderCache(event.orderId(), dto);

			log.info("Redis 캐시 동기화 완료: orderId={}", event.orderId());

		} catch (Exception e) {
			log.error("ORDER_CREATED 이벤트 처리 실패", e);
		}
	}
}
