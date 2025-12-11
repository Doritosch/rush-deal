package com.rushcrew.order_service.infrastructure.adapter.cache;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.order_service.application.command.dto.result.CreateOrderResult;
import com.rushcrew.order_service.application.command.port.out.OrderCachePort;
import com.rushcrew.order_service.application.query.dto.OrderDetailDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCacheAdapter implements OrderCachePort {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	private static final String ORDER_KEY_PREFIX = "order:";
	private static final String USER_ORDERS_KEY_PREFIX = "user:orders:";
	private static final long ORDER_CACHE_TTL = 3600; // 1시간

	@Override
	public void saveOrderCache(CreateOrderResult result) {
		try {
			// 1. 주문 상세 정보 저장 (String)
			String orderKey = ORDER_KEY_PREFIX + result.orderId();
			OrderDetailDto dto = convertToOrderDetailDto(result);

			redisTemplate.opsForValue().set(
				orderKey,
				objectMapper.writeValueAsString(dto),
				ORDER_CACHE_TTL,
				TimeUnit.SECONDS
			);

			// 2. 사용자별 주문 목록에 추가 (Sorted Set)
			String userOrdersKey = USER_ORDERS_KEY_PREFIX + result.userId();
			double score = result.orderedAt().toEpochMilli(); // 타임스탬프를 score로 사용

			redisTemplate.opsForZSet().add(
				userOrdersKey,
				result.orderId().toString(),
				score
			);

			// 3. 사용자별 주문 목록 TTL 설정
			redisTemplate.expire(userOrdersKey, 24, TimeUnit.HOURS);

			log.info("주문 캐시 저장 완료: orderId={}", result.orderId());

		} catch (JsonProcessingException e) {
			log.error("주문 캐시 저장 실패: orderId={}", result.orderId(), e);
		}
	}

	@Override
	public void updateOrderCache(UUID orderId, OrderDetailDto orderDetailDto) {
		try {
			String key = ORDER_KEY_PREFIX + orderId;

			redisTemplate.opsForValue().set(
				key,
				objectMapper.writeValueAsString(orderDetailDto),
				ORDER_CACHE_TTL,
				TimeUnit.SECONDS
			);

			log.info("주문 캐시 업데이트 완료: orderId={}", orderId);

		} catch (JsonProcessingException e) {
			log.error("주문 캐시 업데이트 실패: orderId={}", orderId, e);
		}
	}

	private OrderDetailDto convertToOrderDetailDto(CreateOrderResult result) {
		return OrderDetailDto.builder()
			.orderId(result.orderId())
			.userId(result.userId())
			.orderStatus(result.orderStatus())
			.totalAmount(result.totalAmount())
			.pointUsed(result.pointUsed())
			.finalAmount(result.finalAmount())
			.orderedAt(result.orderedAt())
			.orderItems(result.orderItems())
			.build();
	}
}
