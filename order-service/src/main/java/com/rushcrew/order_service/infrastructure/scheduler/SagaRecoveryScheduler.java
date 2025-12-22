package com.rushcrew.order_service.infrastructure.scheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.port.out.MetricsPort;
import com.rushcrew.order_service.application.port.out.SagaInstancePort;
import com.rushcrew.order_service.application.saga.service.SagaRecoveryService;
import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.enums.SagaStepName;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Saga 타임아웃 복구 스케줄러
 *
 * 조건:
 * - RUNNING 상태
 * - 생성 후 10분 초과
 * - REQUEST_STOCK_RESERVATION 단계에서 응답 대기 중
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SagaRecoveryScheduler {

	private final SagaInstancePort sagaInstancePort;
	private final SagaRecoveryService sagaRecoveryService;
	private final MetricsPort metricsPort; // 추가: 메트릭 기록

	/**
	 * 10분마다 타임아웃된 Saga 복구
	 */
	@Scheduled(fixedDelay = 600_000) // 10분
	public void recoverTimedOutSagas() {

		Instant timeoutThreshold = Instant.now().minus(10, ChronoUnit.MINUTES);

		// 핵심: 이벤트 응답을 기다리다 멈춘 Saga만 조회
		List<SagaInstance> timedOutSagas =
			sagaInstancePort.findTimedOutRunningSagas(
				SagaStatus.RUNNING,
				timeoutThreshold,
				SagaStepName.REQUEST_STOCK_RESERVATION
			);

		if (timedOutSagas.isEmpty()) {
			return;
		}

		log.warn("타임아웃된 Saga {}개 발견", timedOutSagas.size());

		for (SagaInstance saga : timedOutSagas) {
			try {
				log.warn(
					"Saga 타임아웃 처리 시작: sagaId={}, sagaType={}, createdAt={}",
					saga.getSagaId(),
					saga.getSagaType(),
					saga.getCreatedAt() // 생성 시간 로깅 추가
				);

				// 1. 내부 보상 트랜잭션 실행
				sagaRecoveryService.compensateSaga(saga);

				// 2. Saga 상태 저장
				sagaInstancePort.save(saga);

				// 3. 메트릭 기록
				metricsPort.recordSagaTimeout();

				log.info(
					"Saga 타임아웃 처리 완료: sagaId={}",
					saga.getSagaId()
				);

			} catch (Exception e) {
				log.error(
					"Saga 타임아웃 처리 실패: sagaId={}",
					saga.getSagaId(),
					e
				);

				// 실패하더라도 Saga 상태는 FAIL 로 남긴다
				try {
					saga.fail("Saga 타임아웃 - 복구 실패: " + e.getMessage());
					sagaInstancePort.save(saga);

					// 복구 실패도 메트릭 기록 (추가)
					metricsPort.recordSagaRecoveryFailure();

				} catch (Exception saveEx) {
					log.error(
						"Saga 상태 저장 실패: sagaId={}",
						saga.getSagaId(),
						saveEx
					);
				}
			}
		}
	}
}

