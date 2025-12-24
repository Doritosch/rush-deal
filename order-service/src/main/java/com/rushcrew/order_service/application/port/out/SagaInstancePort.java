package com.rushcrew.order_service.application.port.out;

import java.time.Instant;
import java.util.List;

import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.enums.SagaStepName;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;

public interface SagaInstancePort {

	SagaInstance save(SagaInstance sagaInstance);

	/**
	 * RUNNING 상태이면서
	 * createdAt < timeoutThreshold 이고
	 * 마지막 완료 Step이 sagaStepName 인 Saga 조회
	 */
	List<SagaInstance> findTimedOutRunningSagas(
		SagaStatus sagaStatus,
		Instant timeoutThreshold,
		SagaStepName sagaStepName
	);

	SagaInstance findBySagaId(String sagaId);
}
