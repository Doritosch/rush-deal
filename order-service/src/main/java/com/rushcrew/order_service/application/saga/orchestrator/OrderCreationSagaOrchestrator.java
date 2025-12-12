package com.rushcrew.order_service.application.saga.orchestrator;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.command.CreateOrderCommand;
import com.rushcrew.order_service.application.command.dto.result.CreateOrderResult;
import com.rushcrew.order_service.application.saga.dto.OrderCreationSagaData;
import com.rushcrew.order_service.application.saga.dto.SagaContext;
import com.rushcrew.order_service.application.saga.dto.SagaStepResult;
import com.rushcrew.order_service.application.saga.step.CreateOrderStep;
import com.rushcrew.order_service.application.saga.step.DeductPointStep;
import com.rushcrew.order_service.application.saga.step.ReserveStockStep;
import com.rushcrew.order_service.application.saga.step.ValidateStockStep;
import com.rushcrew.order_service.application.port.out.MetricsPort;
import com.rushcrew.order_service.application.port.out.SagaInstancePort;
import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreationSagaOrchestrator {

	private final ValidateStockStep validateStockStep;
	private final ReserveStockStep reserveStockStep;
	private final DeductPointStep deductPointStep;
	private final CreateOrderStep createOrderStep;
	private final SagaInstancePort sagaInstancePort;
	private final MetricsPort metricsPort;

	@Transactional
	public CreateOrderResult execute(CreateOrderCommand command) {
		// 1. Saga 인스턴스 생성 - sagaId 생성
		SagaInstance sagaInstance = SagaInstance.create("ORDER_CREATION", command.userId());
		// DB에 기록
		saveSagaInstance(sagaInstance);
		// 2. Saga Context 생성
		SagaContext context = SagaContext.builder()
			.sagaId(sagaInstance.getSagaId())
			.userId(command.userId())
			.build();
		// 3. Saga Data 생성
		OrderCreationSagaData sagaData = OrderCreationSagaData.builder()
			.command(command)
			.build();

		// 검증 시작
		try {
			// Step 1: 재고 검증
			log.info("[Saga-{}] Step 1: validateStock 시작", context.getSagaId());
			SagaStepResult validateResult = validateStockStep.execute(context, sagaData);
			if (!validateResult.isSuccess()) {
				throw new IllegalArgumentException(validateResult.getErrorMessage());
			}
			sagaInstance.addStep("VALIDATE_STOCK", SagaStatus.COMPLETED);
			saveSagaInstance(sagaInstance);

			// Step 2: 재고 예약
			log.info("[Saga-{}] Step 2: ReserveStock 시작", context.getSagaId());
			SagaStepResult reserveResult = reserveStockStep.execute(context, sagaData);
			if (!reserveResult.isSuccess()) {
				throw new IllegalArgumentException(reserveResult.getErrorMessage());
			}
			sagaInstance.addStep("RESERVE_STOCK", SagaStatus.COMPLETED);
			saveSagaInstance(sagaInstance);

			// Step 3: 포인트 차감
			log.info("[Saga-{}] Step 3: DeductPoint 시작", context.getSagaId());
			SagaStepResult deductResult = deductPointStep.execute(context, sagaData);
			if (!deductResult.isSuccess()) {
				// 포인트 차감 실패 -> 재고 복구
				reserveStockStep.compensate(context, sagaData);
				throw new IllegalArgumentException(deductResult.getErrorMessage());
			}
			sagaInstance.addStep("DEDUCT_POINT", SagaStatus.COMPLETED);
			saveSagaInstance(sagaInstance);

			// Step 4: 주문 생성
			log.info("[Saga-{}] Step 4: CreateOrder 시작", context.getSagaId());
			SagaStepResult createResult = createOrderStep.execute(context, sagaData);
			if (!createResult.isSuccess()) {
				// 주문 생성 실패 → 포인트 환불 + 재고 복구
				deductPointStep.compensate(context, sagaData);
				reserveStockStep.compensate(context, sagaData);
				throw new IllegalArgumentException(createResult.getErrorMessage());
			}
			sagaInstance.addStep("CREATE_ORDER", SagaStatus.COMPLETED);
			saveSagaInstance(sagaInstance);

			// Saga 완료
			sagaInstance.complete();
			saveSagaInstance(sagaInstance);
			metricsPort.recordSagaSuccess(); // 성공 메트릭 기록
			log.info("[Saga-{}] 완료", context.getSagaId());

			// 결과 리턴
			Instant reservationExpiresAt = (Instant) context.getData("reservationExpiresAt");
			if (reservationExpiresAt == null) {
				reservationExpiresAt = Instant.now().plus(15, java.time.temporal.ChronoUnit.MINUTES);
			}

			Instant orderedAt = (Instant) context.getData("orderedAt");
			if (orderedAt == null) {
				orderedAt = Instant.now();
			}

			String orderStatus = (String) context.getData("orderStatus");
			if (orderStatus == null) {
				orderStatus = "PENDING";
			}

			return CreateOrderResult.builder()
				.orderId(sagaData.getOrderId())
				.userId(command.userId())
				.orderStatus(orderStatus)
				.totalAmount(sagaData.getTotalAmount())
				.pointUsed(command.pointUsed())
				.finalAmount(sagaData.getFinalAmount())
				.orderedAt(orderedAt)
				.reservationExpiresAt(reservationExpiresAt)
				.orderItems(sagaData.getOrderItems())
				.build();

		} catch (Exception e) {
			// Saga 실패 처리
			sagaInstance.fail(e.getMessage());
			saveSagaInstance(sagaInstance);
			metricsPort.recordSagaFailure(); // 실패 메트릭 기록
			log.error("[Saga-{}] 실패: {}", context.getSagaId(), e.getMessage(), e);
			throw e;
		}

	}

	private void saveSagaInstance(SagaInstance sagaInstance) {
		sagaInstancePort.save(sagaInstance);
	}
}
