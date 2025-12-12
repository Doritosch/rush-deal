package com.rushcrew.order_service.infrastructure.scheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.port.out.SagaInstancePort;
import com.rushcrew.order_service.application.saga.service.SagaRecoveryService;
import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaRecoveryScheduler {

	private final SagaInstancePort sagaInstancePort;
	private final SagaRecoveryService sagaRecoveryService;

	/**
	 * 10분마다 타임아웃된 Saga 복구
	 */
	@Scheduled(fixedDelay = 600000)	// 10분마다 실행
	public void recoverTimedOutSagas() {
		// 1. 10분 이상 RUNNING 상태인 Saga 조회
		Instant tenMinutesAgo = Instant.now().minus(10, ChronoUnit.MINUTES);
		List<SagaInstance> timedOutSagas = sagaInstancePort
			.findByStatusAndCreatedAtBefore(SagaStatus.RUNNING, tenMinutesAgo);

		if (timedOutSagas.isEmpty()) {
			return;
		}

		log.warn("타임아웃된 Saga {}개 발견", timedOutSagas.size());

		// 각 Saga에 대해 보상 트랜잭션 실행
		for (SagaInstance saga : timedOutSagas) {
			try {
				// 보상 트랜잭션 실행
				sagaRecoveryService.compensateSaga(saga);	// 복구 시도
				sagaInstancePort.save(saga);	// 상태 저장

				log.info("Saga 타임아웃 처리 완료: sagaId={}", saga.getSagaId());

			} catch (Exception e) {
				log.error("Saga 타임아웃 처리 실패: sagaId={}", saga.getSagaId(), e);
				// 실패해도 상태는 업데이트
				try {
					saga.fail("Saga 타임아웃 - 복구 실패: " + e.getMessage());
					sagaInstancePort.save(saga);
				} catch (Exception saveEx) {
					log.error("Saga 상태 저장 실패: sagaId={}", saga.getSagaId(), saveEx);
				}
			}
		}
	}
}
