package com.rushcrew.order_service.application.saga.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rushcrew.order_service.application.command.dto.command.CreateOrderCommand;
import com.rushcrew.order_service.application.saga.dto.OrderCreationSagaData;
import com.rushcrew.order_service.application.saga.dto.SagaContext;
import com.rushcrew.order_service.application.saga.step.CreateOrderStep;
import com.rushcrew.order_service.application.saga.step.DeductPointStep;
import com.rushcrew.order_service.application.saga.step.ReserveStockStep;
import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;
import com.rushcrew.order_service.domain.model.saga.SagaStep;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Saga 복구 서비스
 * 타임아웃된 Saga에 대한 보상 트랜잭션 실행
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SagaRecoveryService {

	private final ReserveStockStep reserveStockStep;
	private final DeductPointStep deductPointStep;

	/* 타임아웃된 Saga에 대한 보상 트랜잭션 실행 */
	@Transactional
	public void compensateSaga(SagaInstance sagaInstance) {
		log.info("Saga 보상 트랜잭션 시작: sagaId={}, sagaType={}",
			sagaInstance.getSagaId(), sagaInstance.getSagaType());

		try {
			// ORDER_CREATION Saga만 처리
			if (!"ORDER_CREATION".equals(sagaInstance.getSagaType())) {
				log.warn("지원하지 않는 Saga 타입: {}", sagaInstance.getSagaType());
				return;
			}

			// 완료된 Step들을 역순으로 보상 트랜잭션 실행
			List<SagaStep> completedSteps = sagaInstance.getSteps().stream()
				.filter(step -> step.getStatus() == SagaStatus.COMPLETED)
				.sorted((s1, s2) -> s2.getExecutedAt().compareTo(s1.getExecutedAt())) // 역순 정렬
				.toList();

			if (completedSteps.isEmpty()) {
				log.info("보상할 완료된 Step이 없음: sagaId={}", sagaInstance.getSagaId());
				sagaInstance.fail("Saga 타임아웃 - 보상할 Step 없음");
				return;
			}

			// SagaContext와 SagaData 재구성 (최소한의 정보만)
			SagaContext context = SagaContext.builder()
				.sagaId(sagaInstance.getSagaId())
				.userId(sagaInstance.getUserId())
				.build();

			// CreateOrderCommand는 복구 시에는 최소한의 정보만 필요
			// 실제로는 DB에서 주문 정보를 조회하여 사용해야 하지만,
			// 타임아웃된 Saga의 경우 주문이 생성되지 않았을 수도 있음
			OrderCreationSagaData sagaData = OrderCreationSagaData.builder()
				.command(CreateOrderCommand.builder()
					.userId(sagaInstance.getUserId())
					.build())
				.build();

			// 각 Step에 대해 역순으로 보상 트랜잭션 실행
			for (SagaStep step : completedSteps) {
				try {
					compensateStep(step.getStepName(), context, sagaData);
					step.markAsCompensated();
					log.info("Step 보상 완료: sagaId={}, stepName={}",
						sagaInstance.getSagaId(), step.getStepName());
				} catch (Exception e) {
					log.error("Step 보상 실패: sagaId={}, stepName={}",
						sagaInstance.getSagaId(), step.getStepName(), e);
					step.markAsFailed("보상 실패: " + e.getMessage());
					// 보상 실패해도 다음 Step 보상 시도
				}
			}

			sagaInstance.fail("Saga 타임아웃 - 보상 트랜잭션 완료");
			log.info("Saga 보상 트랜잭션 완료: sagaId={}", sagaInstance.getSagaId());

		} catch (Exception e) {
			log.error("Saga 보상 트랜잭션 중 오류 발생: sagaId={}", sagaInstance.getSagaId(), e);
			sagaInstance.fail("Saga 타임아웃 - 보상 트랜잭션 실패: " + e.getMessage());
			throw e;
		}
	}

	/* 특정 Step에 대한 보상 트랜잭션 실행 */
	private void compensateStep(String stepName, SagaContext context, OrderCreationSagaData sagaData) {
		switch (stepName) {
			case "DEDUCT_POINT":
				deductPointStep.compensate(context, sagaData);
				break;
			case "RESERVE_STOCK":
				reserveStockStep.compensate(context, sagaData);
				break;
			case "VALIDATE_STOCK":
				// 조회만 수행하므로 보상 불필요
				log.debug("VALIDATE_STOCK은 보상 불필요: sagaId={}", context.getSagaId());
				break;
			default:
				log.warn("알 수 없는 Step 이름: {}, 보상 스킵", stepName);
		}
	}
}

