package com.rushcrew.order_service.application.saga.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rushcrew.order_service.application.saga.dto.SagaContext;
import com.rushcrew.order_service.application.saga.step.DeductPointStep;
import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;
import com.rushcrew.order_service.domain.model.saga.SagaStep;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaRecoveryService {

	private final DeductPointStep deductPointStep;

	@Transactional
	public void compensateSaga(SagaInstance sagaInstance) {
		log.info("Saga 보상 시작: sagaId={}, type={}",
			sagaInstance.getSagaId(), sagaInstance.getSagaType());

		if (!"ORDER_CREATION".equals(sagaInstance.getSagaType())) {
			log.warn("지원하지 않는 Saga 타입: {}", sagaInstance.getSagaType());
			return;
		}

		List<SagaStep> completedSteps = sagaInstance.getSteps().stream()
			.filter(step -> step.getStatus() == SagaStatus.COMPLETED)
			.sorted((a, b) -> b.getExecutedAt().compareTo(a.getExecutedAt()))
			.toList();

		if (completedSteps.isEmpty()) {
			sagaInstance.fail("Saga timeout - no completed steps");
			return;
		}

		SagaContext context = SagaContext.builder()
			.sagaId(sagaInstance.getSagaId())
			.userId(sagaInstance.getUserId())
			.build();

		for (SagaStep step : completedSteps) {
			try {
				compensateStep(step.getStepName(), context);
				step.markAsCompensated();
			} catch (Exception e) {
				log.error("보상 실패: sagaId={}, step={}",
					sagaInstance.getSagaId(), step.getStepName(), e);
				step.markAsFailed(e.getMessage());
			}
		}

		sagaInstance.fail("Saga timeout - compensation finished");
		log.info("Saga 보상 완료: sagaId={}", sagaInstance.getSagaId());
	}

	private void compensateStep(String stepName, SagaContext context) {
		switch (stepName) {

			case "DEDUCT_POINT":
				deductPointStep.compensate(context, null);
				break;

			case "REQUEST_STOCK_RESERVATION":
				// 재고는 타임딜 서비스가 timeout 이벤트로 복구
				log.info("재고 보상은 타임딜 서비스 책임, skip");
				break;

			case "VALIDATE_STOCK":
			case "CREATE_ORDER":
				// 보상 없음
				break;

			default:
				log.warn("알 수 없는 Step: {}", stepName);
		}
	}
}
