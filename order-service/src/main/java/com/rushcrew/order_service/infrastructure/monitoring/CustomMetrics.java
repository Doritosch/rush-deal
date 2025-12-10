package com.rushcrew.order_service.infrastructure.monitoring;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.port.out.MetricsPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomMetrics implements MetricsPort {

	private final MeterRegistry meterRegistry;

	// Saga 성공/실패 카운터
	@Override
	public void recordSagaSuccess() {
		Counter.builder("saga.execution.success")
			.tag("type", "order_creation")
			.register(meterRegistry)
			.increment();
	}

	@Override
	public void recordSagaFailure() {
		Counter.builder("saga.execution.failure")
			.tag("type", "order_creation")
			.register(meterRegistry)
			.increment();
	}

	// Outbox 이벤트 발행 메트릭
	@Override
	public void recordOutboxPublished() {
		Counter.builder("outbox.event.published")
			.register(meterRegistry)
			.increment();
	}

	// Redis 캐시 히트/미스
	@Override
	public void recordCacheHit() {
		Counter.builder("redis.cache.hit")
			.register(meterRegistry)
			.increment();
	}

	@Override
	public void recordCacheMiss() {
		Counter.builder("redis.cache.miss")
			.register(meterRegistry)
			.increment();
	}

	// 주문 생성 소요 시간
	@Override
	public Object startOrderCreationTimer() {
		return Timer.start(meterRegistry);
	}

	@Override
	public void stopOrderCreationTimer(Object sample) {
		if (sample instanceof Timer.Sample timerSample) {
			timerSample.stop(Timer.builder("order.creation.duration")
				.register(meterRegistry));
		}
	}
}
