package com.rushcrew.order_service.infrastructure.adapter.cache;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.order_service.application.query.dto.OrderDetailDto;
import com.rushcrew.order_service.application.query.dto.OrderListDto;
import com.rushcrew.order_service.application.query.dto.OrderSearchCriteria;
import com.rushcrew.order_service.application.query.port.out.OrderQueryPort;
import com.rushcrew.order_service.infrastructure.monitoring.CustomMetrics;
import com.rushcrew.order_service.infrastructure.persistence.repository.OrderJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderQueryAdapter implements OrderQueryPort {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;
	private final OrderJpaRepository orderJpaRepository; // fallback 용
	private final CustomMetrics customMetrics;

	private static final String ORDER_KEY_PREFIX = "order:";

	@Override
	public Optional<OrderDetailDto> findById(UUID orderId) {
		String key = ORDER_KEY_PREFIX + orderId;
		try {
			// 먼저 redis에서 캐시된 데이터 조회
			String cachedData = (String) redisTemplate.opsForValue().get(key);
			if (cachedData != null) {
				customMetrics.recordCacheHit(); // 캐시 히트 메트릭 기록
				log.info("Redis에서 주문 조회 성공: orderId={}", orderId);
				return Optional.of(objectMapper.readValue(cachedData, OrderDetailDto.class));
			}
			// cache miss - DB에서 조회 후 캐싱
			customMetrics.recordCacheMiss(); // 캐시 미스 메트릭 기록
			log.info("Redis Cache Miss - DB에서 조회: orderId={}", orderId);
			return orderJpaRepository.findById(orderId)
				.map(order -> {
					OrderDetailDto dto = OrderDetailDto.fromEntity(order);
					// Redis에 캐싱
					try {
						redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(dto));
					} catch (JsonProcessingException e) {
						log.error("Redis 캐싱 실패", e);
					}
					return dto;
				});
		} catch (JsonProcessingException e) {
			log.error("Redis 데이터 역직렬화 실패: orderId={}", orderId, e);
			// DB로 fallback
			return orderJpaRepository.findById(orderId).map(OrderDetailDto::fromEntity);
		}
	}

	@Override
	public Page<OrderListDto> findByCriteria(OrderSearchCriteria criteria, Pageable pageable) {
		// TODO: 추후 Redis Sorted Set 등으로 최적화
		log.info("주문 목록 조회 (DB): userId={}", criteria.getUserId());
		return orderJpaRepository.findByUserId(criteria.getUserId(), pageable)
			.map(OrderListDto::fromEntity);
	}
}
