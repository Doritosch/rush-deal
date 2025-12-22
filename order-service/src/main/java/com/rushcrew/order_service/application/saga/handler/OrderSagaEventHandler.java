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
			log.warn("[Saga-{}] 이미 완료 또는 실패한 Saga입니다. 현재 상태: {}",
				event.sagaId(), saga.getStatus());
			return;
		}

		// 반드시 restore
		SagaContext context = SagaContext.restore(saga);
		OrderCreationSagaData data = saga.restoreData();

		try {
			// Step 3: 주문 생성 (포인트 X)
			createOrderStep.execute(context, data, event);
			saga.addStep(SagaStepName.CREATE_ORDER, SagaStatus.COMPLETED);

			saga.complete();
			sagaInstancePort.save(saga);

			metricsPort.recordSagaSuccess();
			log.info("[Saga-{}] 주문 생성 완료", event.sagaId());

		} catch (Exception e) {
			log.error("[Saga-{}] 주문 생성 실패: {}", event.sagaId(), e.getMessage(), e);
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
			log.warn("[Saga-{}] 이미 완료 또는 실패한 Saga입니다. 현재 상태: {}", event.sagaId(), saga.getStatus());
			return;
		}

		log.error("[Saga-{}] 재고 예약 실패: {}", event.sagaId(), event.reason());
		saga.fail(event.reason());
		sagaInstancePort.save(saga);
		metricsPort.recordSagaFailure();
	}
}
