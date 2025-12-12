package com.rushcrew.order_service.infrastructure.persistence.saga;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.port.out.SagaInstancePort;
import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;
import com.rushcrew.order_service.infrastructure.persistence.saga.repository.SagaInstanceJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SagaInstanceAdapter implements SagaInstancePort {

	private final SagaInstanceJpaRepository sagaInstanceJpaRepository;

	@Override
	public SagaInstance save(SagaInstance sagaInstance) {
		return sagaInstanceJpaRepository.save(sagaInstance);
	}

	@Override
	public List<SagaInstance> findByStatusAndCreatedAtBefore(SagaStatus status, Instant createdAt) {
		return sagaInstanceJpaRepository.findByStatusAndCreatedAtBefore(status, createdAt);
	}
}
