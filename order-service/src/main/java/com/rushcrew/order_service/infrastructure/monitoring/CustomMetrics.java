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
			.description("성공적으로 완료된 Saga 수")
			.register(meterRegistry)
			.increment();
	}

	@Override
	public void recordSagaFailure() {
		Counter.builder("saga.execution.failure")
			.tag("type", "order_creation")
			.description("실패한 Saga 수 (비즈니스 로직 실패)")
			.register(meterRegistry)
			.increment();
	}

	// Saga 타임아웃 메트릭
	@Override
	public void recordSagaTimeout() {
		Counter.builder("saga.execution.timeout")
			.tag("type", "order_creation")
			.tag("reason", "stock_reservation_timeout")
			.description("10분 이상 응답 없어 타임아웃 처리된 Saga 수")
			.register(meterRegistry)
			.increment();
	}

	// Saga 복구 실패 메트릭
	@Override
	public void recordSagaRecoveryFailure() {
		Counter.builder("saga.recovery.failure")
			.tag("type", "order_creation")
			.description("타임아웃 Saga 복구(보상) 실패 수")
			.register(meterRegistry)
			.increment();
	}

	// Outbox 이벤트 발행 메트릭
	@Override
	public void recordOutboxPublished() {
		Counter.builder("outbox.event.published")
			.description("Outbox 이벤트 발행 성공 수")
			.register(meterRegistry)
			.increment();
	}

	// Redis 캐시 히트/미스
	@Override
	public void recordCacheHit() {
		Counter.builder("redis.cache.hit")
			.description("Redis 캐시 히트 수")
			.register(meterRegistry)
			.increment();
	}

	@Override
	public void recordCacheMiss() {
		Counter.builder("redis.cache.miss")
			.description("Redis 캐시 미스 수")
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
				.description("주문 생성 API 처리 소요 시간")
				.register(meterRegistry));
		}
	}
}
