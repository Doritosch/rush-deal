package com.rushcrew.order_service.domain.model.saga;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.rushcrew.order_service.domain.enums.SagaStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_saga_instance", schema = "order_schema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SagaInstance {

	@Id
	private UUID sagaId;

	@Column(nullable = false)
	private UUID orderId;	// 주문 ID 추가

	@Column(nullable = false, length = 50)
	private String sagaType;

	@Column(nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private SagaStatus status;

	@Column(nullable = false)
	private Instant createdAt;

	private Instant completedAt;

	private Instant failedAt;

	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	@OneToMany(mappedBy = "sagaInstance", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<SagaStep> steps = new ArrayList<>();

	public static SagaInstance create(String sagaType, Long userId) {
		return SagaInstance.builder()
			.sagaId(UUID.randomUUID())
			.sagaType(sagaType)
			.userId(userId)
			.status(SagaStatus.RUNNING)
			.createdAt(Instant.now())
			.build();
	}

	public void addStep(String stepName, SagaStatus status) {
		SagaStep step = SagaStep.create(this, stepName, status);
		this.steps.add(step);
	}

	public void complete() {
		this.status = SagaStatus.COMPLETED;
		this.completedAt = Instant.now();
	}

	public void fail(String errorMessage) {
		this.status = SagaStatus.FAILED;
		this.failedAt = Instant.now();
		this.errorMessage = errorMessage;
	}

	public void startCompensation() {
		this.status = SagaStatus.COMPENSATING;
	}
}
