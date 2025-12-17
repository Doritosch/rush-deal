package com.rushcrew.order_service.infrastructure.messaging.consumer;

import java.util.Map;
import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.order_service.application.command.port.out.OrderCachePort;
import com.rushcrew.order_service.application.query.port.out.OrderQueryPort;
import com.rushcrew.order_service.infrastructure.messaging.event.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

	private final OrderCachePort orderCachePort;
	private final OrderQueryPort orderQueryPort;
	private final ObjectMapper objectMapper;

	/**
	 * ORDER_CREATED 이벤트
	 * → DB에서 완전한 주문 정보 조회 후 캐시 생성
	 */
	@KafkaListener(topics = "order.created", groupId = "order-cache-sync")
	public void handleOrderCreatedEvent(String message) {
		try {
			OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
			UUID orderId = event.orderId();

			// 멱등성 체크
			if (orderCachePort.existsInCache(orderId)) {
				log.info("이미 캐시에 존재하는 주문: orderId={}", orderId);
				return;
			}

			// DB에서 완전한 주문 정보 조회
			orderQueryPort.findOrderDetail(orderId)
				.ifPresentOrElse(
					dto -> {
						orderCachePort.updateOrderCache(orderId, dto);
						log.info("ORDER_CREATED 캐시 생성 완료: orderId={}, status={}",
							orderId, dto.getOrderStatus());
					},
					() -> {
						// 이벤트는 받았지만 DB에서 아직 조회 안 됨 (트랜잭션 커밋 전)
						log.warn("주문 생성 이벤트 수신했으나 DB 조회 실패: orderId={}", orderId);
						throw new RuntimeException("Order not found in DB: " + orderId);
					}
				);

		} catch (JsonProcessingException e) {
			log.error("ORDER_CREATED 이벤트 파싱 실패: {}", message, e);
			// 파싱 실패는 재시도 무의미
		} catch (Exception e) {
			log.error("ORDER_CREATED 캐시 동기화 실패", e);
			throw new RuntimeException(e); // 재시도 트리거
		}
	}

	/**
	 * 주문 상태 변경 이벤트
	 * → DB 조회 후 캐시 갱신
	 */
	@KafkaListener(
		topics = {
			"order.paid",
			"order.purchase.confirmed",
			"order.cancelled",
			"order.refunded",
			"order.updated"
		},
		groupId = "order-cache-sync"
	)
	public void handleOrderStatusChangedEvent(String message) {
		try {
			Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {});
			UUID orderId = UUID.fromString(payload.get("orderId").toString());

			orderQueryPort.findOrderDetail(orderId)
				.ifPresentOrElse(
					dto -> {
						orderCachePort.updateOrderCache(orderId, dto);
						log.info("주문 상태 변경 캐시 갱신 완료: orderId={}, status={}",
							orderId, dto.getOrderStatus());
					},
					() -> log.warn("주문 조회 실패로 캐시 갱신 생략: orderId={}", orderId)
				);

		} catch (JsonProcessingException e) {
			log.error("주문 상태 변경 이벤트 파싱 실패: {}", message, e);
		} catch (Exception e) {
			log.error("주문 상태 변경 캐시 동기화 실패", e);
			throw new RuntimeException(e);
		}
	}
}
