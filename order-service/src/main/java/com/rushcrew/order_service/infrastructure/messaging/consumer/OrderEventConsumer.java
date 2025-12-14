package com.rushcrew.order_service.infrastructure.messaging.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
	 *
	 * 멱등성: Redis는 덮어쓰기만 하므로 중복 처리해도 문제없음
	 * 재시도: Redis 장애 시 자동 재시도 (application.yml 설정)
	 */
	@KafkaListener(topics = "order.created", groupId = "order-cache-sync")
	public void handleOrderCreatedEvent(String message) {
		try {
			log.info("ORDER_CREATED 이벤트 수신");

			OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);

			// 멱등성 체크
			if (orderCachePort.existsInCache(event.orderId())) {
				log.info("이미 캐시에 존재하는 주문, 업데이트 진행: orderId={}", event.orderId());
			}

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

		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
			log.error("ORDER_CREATED 이벤트 파싱 실패: message={}", message, e);
			// JSON 파싱 실패는 재시도해도 소용없으므로 무시

		} catch (Exception e) {
			log.error("ORDER_CREATED 이벤트 처리 실패", e);
			// Redis 장애 등 → 재시도 발동
			throw new RuntimeException("Redis 캐시 동기화 실패", e);
		}
	}
}
