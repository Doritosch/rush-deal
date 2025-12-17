package com.rushcrew.order_service.infrastructure.scheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.port.out.OrderCachePort;
import com.rushcrew.order_service.application.query.port.out.OrderQueryPort;
import com.rushcrew.order_service.infrastructure.persistence.repository.OrderJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheWarmingScheduler {

	private final OrderJpaRepository orderRepository;
	private final OrderCachePort orderCachePort;
	private final OrderQueryPort orderQueryPort;

	/**
	 * 애플리케이션 시작 시 최근 주문 캐시 Warming
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void warmupCacheOnStartup() {
		log.info("Cache Warming 시작");

		try {
			// 최근 24시간 이내 주문 ID 조회
			Instant oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS);
			List<UUID> recentOrderIds = orderRepository.findRecentOrderIds(oneDayAgo);

			int cachedCount = 0;
			for (UUID orderId : recentOrderIds) {
				try {
					// OrderQueryPort를 통해 완전한 DTO 조회
					orderQueryPort.findOrderDetail(orderId)
						.ifPresent(dto -> {
							orderCachePort.updateOrderCache(orderId, dto);
						});
					cachedCount++;
				} catch (Exception e) {
					log.warn("주문 캐싱 실패: orderId={}", orderId, e);
				}
			}

			log.info("Cache Warming 완료: {}개 주문 캐싱", cachedCount);

		} catch (Exception e) {
			log.error("Cache Warming 실패", e);
		}
	}

	/**
	 * 매 시간마다 Hot Data 재캐싱
	 */
	@Scheduled(cron = "0 0 * * * ?") // 매 시간 정각
	public void refreshHotDataCache() {
		log.info("Hot Data Cache 갱신 시작");

		try {
			// 최근 1시간 이내 주문 ID 조회
			Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
			List<UUID> hotOrderIds = orderRepository.findRecentOrderIds(oneHourAgo);

			int refreshedCount = 0;
			for (UUID orderId : hotOrderIds) {
				try {
					// OrderQueryPort를 통해 완전한 DTO 조회
					orderQueryPort.findOrderDetail(orderId)
						.ifPresent(dto -> {
							orderCachePort.updateOrderCache(orderId, dto);
						});
					refreshedCount++;
				} catch (Exception e) {
					log.warn("Hot Data 캐시 갱신 실패: orderId={}", orderId, e);
				}
			}

			log.info("Hot Data Cache 갱신 완료: {}개 주문", refreshedCount);

		} catch (Exception e) {
			log.error("Hot Data Cache 갱신 실패", e);
		}
	}
}
