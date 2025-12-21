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
import com.rushcrew.order_service.infrastructure.monitoring.CustomMetrics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 주문 이벤트 소비자 - 실시간 캐시 동기화
 *
 * 역할:
 * - 주문 생성/변경 이벤트 수신 → 즉시 캐시 갱신
 * - CacheWarmingScheduler와 함께 2-tier 캐싱 전략 구성
 *   1. EventConsumer: 실시간 변경사항 즉시 반영 (< 1초)
 *   2. Scheduler: 정기적 대량 갱신 (6시간 주기)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

	private final OrderCachePort orderCachePort;
	private final OrderQueryPort orderQueryPort;
	private final ObjectMapper objectMapper;
	private final CustomMetrics customMetrics;

	/**
	 * ORDER_CREATED 이벤트
	 * → DB에서 완전한 주문 정보 조회 후 캐시 생성
	 *
	 * 장점:
	 * - 사용자가 주문 직후 조회 시 캐시 Hit (빠른 응답)
	 * - 스케줄러 대비 최대 6시간 빠른 캐시 반영
	 */
	@KafkaListener(topics = "order.created", groupId = "order-cache-sync")
	public void handleOrderCreatedEvent(String message) {
		long startTime = System.currentTimeMillis();
		String orderIdStr = null;

		try {
			OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
			final UUID orderId = event.orderId();
			orderIdStr = orderId.toString();

			log.info("[EventConsumer] ORDER_CREATED 이벤트 수신: orderId={}", orderId);

			// 멱등성 체크
			if (orderCachePort.existsInCache(orderId)) {
				log.info("[EventConsumer] 이미 캐시에 존재 (멱등성): orderId={}", orderId);
				customMetrics.recordCacheSyncSkipped("ORDER_CREATED");
				return;
			}

			// DB에서 완전한 주문 정보 조회
			orderQueryPort.findOrderDetail(orderId)
				.ifPresentOrElse(
					dto -> {
						orderCachePort.updateOrderCache(orderId, dto);
						long duration = System.currentTimeMillis() - startTime;

						log.info("[EventConsumer] 캐시 생성 완료: orderId={}, status={}, duration={}ms",
							orderId, dto.getOrderStatus(), duration);

						customMetrics.recordCacheSyncSuccess("ORDER_CREATED", duration);
					},
					() -> {
						// 트랜잭션 커밋 전이거나 DB 복제 지연
						log.warn("[EventConsumer] DB 조회 실패 (트랜잭션 대기 중?): orderId={}", orderId);
						customMetrics.recordCacheSyncFailure("ORDER_CREATED", "DB_NOT_FOUND");
						throw new RuntimeException("Order not found in DB: " + orderId);
					}
				);

		} catch (JsonProcessingException e) {
			// orderId가 추출 안 됐을 수 있으므로 orderIdStr 사용
			log.error("[EventConsumer] 이벤트 파싱 실패 (orderId={}): message={}",
				orderIdStr != null ? orderIdStr : "unknown", message, e);
			customMetrics.recordCacheSyncFailure("ORDER_CREATED", "PARSE_ERROR");
			// 파싱 실패는 재시도 무의미하므로 예외 안 던짐

		} catch (Exception e) {
			log.error("[EventConsumer] 캐시 동기화 실패: orderId={}",
				orderIdStr != null ? orderIdStr : "unknown", e);
			customMetrics.recordCacheSyncFailure("ORDER_CREATED", "UNKNOWN_ERROR");
			throw new RuntimeException(e); // 재시도 트리거
		}
	}

	/**
	 * 주문 상태 변경 이벤트
	 * → DB 조회 후 캐시 갱신
	 *
	 * 처리 이벤트:
	 * - order.paid: 결제 완료
	 * - order.purchase.confirmed: 구매 확정
	 * - order.cancelled: 주문 취소
	 * - order.refunded: 환불 완료
	 * - order.updated: 주문 정보 수정
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
		long startTime = System.currentTimeMillis();
		String orderIdStr = null;
		String eventType = "UNKNOWN"; // catch용

		try {
			Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {});
			eventType = extractEventTypeFromPayload(payload);
			// eventType 먼저 추출 (로깅용)
			final String finalEventType = eventType; // 람다용
			// orderId 추출
			final UUID orderId = UUID.fromString(payload.get("orderId").toString());
			orderIdStr = orderId.toString(); // 로깅용

			log.info("[EventConsumer] {} 이벤트 수신: orderId={}", eventType, orderId);

			orderQueryPort.findOrderDetail(orderId)
				.ifPresentOrElse(
					dto -> {
						orderCachePort.updateOrderCache(orderId, dto);
						long duration = System.currentTimeMillis() - startTime;

						log.info("[EventConsumer] 캐시 갱신 완료: orderId={}, status={}, eventType={}, duration={}ms",
							orderId, dto.getOrderStatus(), finalEventType, duration);

						customMetrics.recordCacheSyncSuccess(finalEventType, duration);
					},
					() -> {
						log.warn("[EventConsumer] DB 조회 실패로 캐시 갱신 생략: orderId={}, eventType={}",
							orderId, finalEventType);
						customMetrics.recordCacheSyncFailure(finalEventType, "DB_NOT_FOUND");
					}
				);

		} catch (JsonProcessingException e) {
			log.error("[EventConsumer] 이벤트 파싱 실패 (eventType={}, orderId={}): message={}",
				eventType, orderIdStr != null ? orderIdStr : "unknown", message, e);
			customMetrics.recordCacheSyncFailure(eventType, "PARSE_ERROR");

		} catch (Exception e) {
			log.error("[EventConsumer] 캐시 갱신 실패: orderId={}, eventType={}",
				orderIdStr != null ? orderIdStr : "unknown", eventType, e);
			customMetrics.recordCacheSyncFailure(eventType, "UNKNOWN_ERROR");
			throw new RuntimeException(e); // 재시도 트리거
		}
	}

	/**
	 * 페이로드에서 이벤트 타입 추출 (로깅/메트릭용)
	 */
	private String extractEventTypeFromPayload(Map<String, Object> payload) {
		try {
			// Kafka 헤더나 페이로드에서 이벤트 타입 추출 시도
			return payload.getOrDefault("eventType", "UNKNOWN").toString();
		} catch (Exception e) {
			return "UNKNOWN";
		}
	}
}
