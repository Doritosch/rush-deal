package com.rushcrew.order_service.infrastructure.monitoring;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rushcrew.order_service.infrastructure.persistence.outbox.entity.OutboxEventEntity;
import com.rushcrew.order_service.infrastructure.persistence.outbox.repository.OutboxEventJpaRepository;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxMetrics {

	private final OutboxEventJpaRepository outboxRepository;
	private final MeterRegistry meterRegistry;

	@Scheduled(fixedDelay = 60000) // 1분마다
	public void recordMetrics() {
		long pendingCount = outboxRepository.countByStatus(OutboxEventEntity.OutboxStatus.PENDING);
		long failedCount = outboxRepository.countByStatus(OutboxEventEntity.OutboxStatus.FAILED);

		meterRegistry.gauge("outbox.pending.count", pendingCount);
		meterRegistry.gauge("outbox.failed.count", failedCount);

		if (pendingCount > 100) {
			log.warn("Outbox PENDING 이벤트가 {}개로 많습니다!", pendingCount);
		}

		if (failedCount > 10) {
			log.error("Outbox FAILED 이벤트가 {}개입니다. 확인이 필요합니다!", failedCount);
		}
	}
}
