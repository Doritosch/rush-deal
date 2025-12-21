package com.rushcrew.order_service.domain.model.saga;

import java.time.Instant;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.order_service.application.saga.dto.OrderCreationSagaData;
import com.rushcrew.order_service.domain.enums.SagaStatus;
import com.rushcrew.order_service.domain.enums.SagaStepName;

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

	@Column(nullable = true)
	private UUID orderId;

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

	@Column(columnDefinition = "TEXT")
	private String sagaData; // JSON 형태로 저장

	@Transient
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static SagaInstance create(String sagaType, Long userId) {
		return SagaInstance.builder()
			.sagaId(UUID.randomUUID())
			.sagaType(sagaType)
			.userId(userId)
			.status(SagaStatus.RUNNING)
			.createdAt(Instant.now())
			.build();
	}

	public void addStep(SagaStepName stepName, SagaStatus status) {
		this.steps.add(SagaStep.create(this, stepName.name(), status));
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

	public boolean isCompleted() {
		return status == SagaStatus.COMPLETED;
	}

	public boolean isFailed() {
		return status == SagaStatus.FAILED;
	}

	// SagaData 저장
	public void saveData(OrderCreationSagaData data) {
		try {
			this.sagaData = objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("SagaData 직렬화 실패", e);
		}
	}

	// SagaData 복원
	public OrderCreationSagaData restoreData() {
		if (this.sagaData == null) return null;
		try {
			return objectMapper.readValue(this.sagaData, OrderCreationSagaData.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("SagaData 역직렬화 실패", e);
		}
	}
}
