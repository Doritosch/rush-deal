package com.rushcrew.order_service.application.saga.orchestrator;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.command.CreateOrderCommand;
import com.rushcrew.order_service.application.port.out.SagaInstancePort;
import com.rushcrew.order_service.application.saga.dto.OrderCreationSagaData;
import com.rushcrew.order_service.application.saga.dto.SagaContext;
import com.rushcrew.order_service.application.saga.step.RequestStockReservationStep;
import com.rushcrew.order_service.application.saga.step.ValidateStockStep;
import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.enums.SagaStepName;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;
import com.rushcrew.order_service.application.saga.dto.SagaStepResult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreationSagaOrchestrator {

	private final ValidateStockStep validateStockStep;
	private final RequestStockReservationStep requestStockReservationStep;
	private final SagaInstancePort sagaInstancePort;

	@Transactional
	public UUID execute(CreateOrderCommand command) {
		// 1. Saga 생성
		SagaInstance saga = SagaInstance.create(SagaStepName.CREATE_ORDER.name(), command.userId());
		sagaInstancePort.save(saga);

		// 2. Context / Data 구성
		SagaContext context = SagaContext.builder()
			.sagaId(saga.getSagaId())
			.userId(command.userId())
			.build();

		OrderCreationSagaData data = OrderCreationSagaData.builder()
			.command(command)
			.build();

		// Step 1: ValidateStock
		SagaStepResult validateResult = validateStockStep.execute(context, data);
		if (validateResult.isFailure()) {
			saga.fail(validateResult.getErrorMessage());
			sagaInstancePort.save(saga);
			throw new IllegalArgumentException(validateResult.getErrorMessage());
		}
		saga.addStep(SagaStepName.VALIDATE_STOCK, SagaStatus.COMPLETED);

		// Step 2: RequestStockReservation
		requestStockReservationStep.execute(context, data);
		saga.addStep(SagaStepName.REQUEST_STOCK_RESERVATION, SagaStatus.WAITING);

		// Step 완료 후 SagaData 저장
		saga.saveData(data);

		// Saga 저장
		sagaInstancePort.save(saga);

		log.info("[Saga-{}] ORDER_CREATION Saga 시작", saga.getSagaId());

		return saga.getSagaId();
	}
}
