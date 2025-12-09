package com.rushcrew.order_service.domain.model.saga;

import java.time.Instant;
import java.util.UUID;

import com.rushcrew.order_service.domain.enums.SagaStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_saga_step", schema = "order_schema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SagaStep {

	@Id
	private UUID sagaStepId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "saga_id", nullable = false)
	private SagaInstance sagaInstance;

	@Column(nullable = false, length = 50)
	private String stepName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private SagaStatus status;

	private Instant executedAt;

	private Instant compensatedAt;

	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	public static SagaStep create(SagaInstance sagaInstance, String stepName, SagaStatus status) {
		return SagaStep.builder()
			.sagaStepId(UUID.randomUUID())
			.sagaInstance(sagaInstance)
			.stepName(stepName)
			.status(status)
			.executedAt(Instant.now())
			.build();
	}

	public void markAsCompensated() {
		this.status = SagaStatus.COMPENSATED;
		this.compensatedAt = Instant.now();
	}

	public void markAsFailed(String errorMessage) {
		this.status = SagaStatus.FAILED;
		this.errorMessage = errorMessage;
	}
}
