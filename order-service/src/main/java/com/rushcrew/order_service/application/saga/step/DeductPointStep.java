package com.rushcrew.order_service.application.saga.step;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.port.out.PointEventPort;
import com.rushcrew.order_service.application.saga.dto.OrderCreationSagaData;
import com.rushcrew.order_service.application.saga.dto.SagaContext;
import com.rushcrew.order_service.application.saga.dto.SagaStepResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeductPointStep {

	private final PointEventPort pointEventPort;

	public SagaStepResult execute(SagaContext context, OrderCreationSagaData data) {
		if (data.getCommand().pointUsed() == null || data.getCommand().pointUsed() <= 0) {
			log.info("[Saga-{}] 포인트 사용 없음, 차감 스킵", context.getSagaId());
			return SagaStepResult.success();
		}

		if (data.getOrderId() == null) {
			return SagaStepResult.failure("OrderId가 바인딩되지 않아 포인트 차감 불가");
		}

		try {
			log.info("[Saga-{}] 포인트 차감 요청: userId={}, point={}",
				context.getSagaId(),
				data.getCommand().userId(),
				data.getCommand().pointUsed()
			);

			pointEventPort.publishPointDeductRequested(
				data.getCommand().userId(),
				data.getOrderId(),
				data.getCommand().pointUsed(),
				"주문 결제용"
			);

			log.info("[Saga-{}] 포인트 차감 이벤트 발행 완료", context.getSagaId());
			return SagaStepResult.success();

		} catch (Exception e) {
			log.error("[Saga-{}] 포인트 차감 실패", context.getSagaId(), e);
			return SagaStepResult.failure("포인트 차감 실패: " + e.getMessage());
		}
	}

	public void compensate(SagaContext context, OrderCreationSagaData data) {
		if (data.getCommand().pointUsed() == null || data.getCommand().pointUsed() <= 0 || data.getOrderId() == null) {
			return;
		}

		log.info("[Saga-{}] 포인트 복원 요청: userId={}, point={}",
			context.getSagaId(),
			data.getCommand().userId(),
			data.getCommand().pointUsed()
		);

		pointEventPort.publishPointRefundRequested(
			data.getCommand().userId(),
			data.getOrderId(),
			data.getCommand().pointUsed(),
			"주문 실패 복원"
		);
	}
}
