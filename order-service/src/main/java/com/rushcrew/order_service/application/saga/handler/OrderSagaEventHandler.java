package com.rushcrew.order_service.application.saga.handler;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rushcrew.order_service.application.port.out.MetricsPort;
import com.rushcrew.order_service.application.port.out.SagaInstancePort;
import com.rushcrew.order_service.application.saga.dto.OrderCreationSagaData;
import com.rushcrew.order_service.application.saga.dto.SagaContext;
import com.rushcrew.order_service.application.saga.step.CreateOrderStep;
import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.enums.SagaStepName;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;
import com.rushcrew.order_service.infrastructure.messaging.event.StockReservationFailedEvent;
import com.rushcrew.order_service.infrastructure.messaging.event.StockReservedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaEventHandler {

	private final SagaInstancePort sagaInstancePort;
	private final CreateOrderStep createOrderStep;
	private final MetricsPort metricsPort;

	@Transactional
	public void handleStockReserved(StockReservedEvent event) {
		SagaInstance saga = sagaInstancePort.findBySagaId(event.sagaId());

		if (saga.isCompleted() || saga.isFailed()) {
			return;
		}

		// 반드시 restore
		SagaContext context = SagaContext.restore(saga);
		OrderCreationSagaData data = saga.restoreData();

		try {
			// Step 3: 주문 생성 (포인트 X)
			createOrderStep.execute(context, data);
			saga.addStep(SagaStepName.CREATE_ORDER, SagaStatus.COMPLETED);

			saga.complete();
			sagaInstancePort.save(saga);

			metricsPort.recordSagaSuccess();

		} catch (Exception e) {
			saga.fail(e.getMessage());
			sagaInstancePort.save(saga);
			metricsPort.recordSagaFailure();
			throw e;
		}
	}

	@Transactional
	public void handleStockReservationFailed(StockReservationFailedEvent event) {
		SagaInstance saga = sagaInstancePort.findBySagaId(event.sagaId());

		if (saga.isCompleted() || saga.isFailed()) {
			return;
		}

		saga.fail(event.reason());
		sagaInstancePort.save(saga);
		metricsPort.recordSagaFailure();
	}
}
