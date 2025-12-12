package com.rushcrew.order_service.application.port.out;

import java.time.Instant;
import java.util.List;

import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;

public interface SagaInstancePort {
	SagaInstance save(SagaInstance sagaInstance);

	List<SagaInstance> findByStatusAndCreatedAtBefore(SagaStatus status, Instant createdAt);
}
