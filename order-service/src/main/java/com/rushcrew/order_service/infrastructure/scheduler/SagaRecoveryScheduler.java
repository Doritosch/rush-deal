package com.rushcrew.order_service.infrastructure.scheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.saga.service.SagaRecoveryService;
import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;
import com.rushcrew.order_service.infrastructure.persistence.saga.repository.SagaInstanceJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaRecoveryScheduler {

	private final SagaInstanceJpaRepository sagaRepository;
	private final SagaRecoveryService sagaRecoveryService;

	/**
	 * 10분마다 타임아웃된 Saga 복구
	 */
	@Scheduled(fixedDelay = 600000)
	public void recoverTimedOutSagas() {
		Instant tenMinutesAgo = Instant.now().minus(10, ChronoUnit.MINUTES);

		List<SagaInstance> timedOutSagas = sagaRepository
			.findByStatusAndCreatedAtBefore(SagaStatus.RUNNING, tenMinutesAgo);

		if (timedOutSagas.isEmpty()) {
			return;
		}

		log.warn("타임아웃된 Saga {}개 발견", timedOutSagas.size());

		for (SagaInstance saga : timedOutSagas) {
			try {
				// 보상 트랜잭션 실행
				sagaRecoveryService.compensateSaga(saga);
				sagaRepository.save(saga);

				log.info("Saga 타임아웃 처리 완료: sagaId={}", saga.getSagaId());

			} catch (Exception e) {
				log.error("Saga 타임아웃 처리 실패: sagaId={}", saga.getSagaId(), e);
				// 실패해도 상태는 업데이트
				try {
					saga.fail("Saga 타임아웃 - 복구 실패: " + e.getMessage());
					sagaRepository.save(saga);
				} catch (Exception saveEx) {
					log.error("Saga 상태 저장 실패: sagaId={}", saga.getSagaId(), saveEx);
				}
			}
		}
	}
}
