package com.rushcrew.order_service.application.port.out;

/**
 * 메트릭 수집을 위한 Port (Application 계층)
 */
public interface MetricsPort {

	/**
	 * Saga 성공 메트릭 기록
	 */
	void recordSagaSuccess();

	/**
	 * Saga 실패 메트릭 기록
	 */
	void recordSagaFailure();

	/**
	 * Outbox 이벤트 발행 메트릭 기록
	 */
	void recordOutboxPublished();

	/**
	 * Redis 캐시 히트 메트릭 기록
	 */
	void recordCacheHit();

	/**
	 * Redis 캐시 미스 메트릭 기록
	 */
	void recordCacheMiss();

	/**
	 * 주문 생성 타이머 시작
	 * @return Timer.Sample (타이머 샘플)
	 */
	Object startOrderCreationTimer();

	/**
	 * 주문 생성 타이머 종료
	 * @param sample 타이머 샘플
	 */
	void stopOrderCreationTimer(Object sample);

	void recordSagaTimeout();

	void recordSagaRecoveryFailure();
}

