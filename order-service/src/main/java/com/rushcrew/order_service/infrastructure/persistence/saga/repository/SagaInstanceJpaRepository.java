package com.rushcrew.order_service.infrastructure.persistence.saga.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;

public interface SagaInstanceJpaRepository extends JpaRepository<SagaInstance, UUID> {

	@Query("""
		select distinct s
		from SagaInstance s
		join s.steps step
		where s.status = :status
		  and s.createdAt < :timeout
		  and step.status = 'COMPLETED'
		  and step.executedAt = (
		      select max(s2.executedAt)
		      from SagaStep s2
		      where s2.sagaInstance = s
		  )
		  and step.stepName = :stepName
	""")
	List<SagaInstance> findTimedOutSagas(
		SagaStatus status,
		Instant timeout,
		String stepName
	);
}
