package com.rushcrew.order_service.infrastructure.monitoring;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rushcrew.order_service.infrastructure.persistence.outbox.entity.OutboxEventEntity;
import com.rushcrew.order_service.infrastructure.persistence.outbox.repository.OutboxEventJpaRepository;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxMetrics {

	private final OutboxEventJpaRepository outboxRepository;
	private final MeterRegistry meterRegistry;

	@Getter
	private long currentPendingCount = 0;
	@Getter
	private long currentFailedCount = 0;

	@PostConstruct
	public void initMetrics() {
		Gauge.builder("outbox.pending.count", this, OutboxMetrics::getCurrentPendingCount)
			.description("발행 대기 중인 Outbox 이벤트 수")
			.register(meterRegistry);

		Gauge.builder("outbox.failed.count", this, OutboxMetrics::getCurrentFailedCount)
			.description("발행 실패한 Outbox 이벤트 수")
			.register(meterRegistry);
	}

	@Scheduled(fixedDelay = 60000)
	public void recordMetrics() {
		try {
			currentPendingCount =
				outboxRepository.countByStatus(OutboxEventEntity.OutboxStatus.PENDING);
			currentFailedCount =
				outboxRepository.countByStatus(OutboxEventEntity.OutboxStatus.FAILED);

			log.debug("Outbox 메트릭 업데이트: PENDING={}, FAILED={}",
				currentPendingCount, currentFailedCount);

			if (currentPendingCount > 100) {
				log.warn("⚠️ Outbox PENDING 이벤트 {}개", currentPendingCount);
			}

			if (currentFailedCount > 10) {
				log.error("🚨 Outbox FAILED 이벤트 {}개", currentFailedCount);
			}

		} catch (Exception e) {
			log.error("Outbox 메트릭 수집 실패", e);
		}
	}

}
