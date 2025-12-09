package com.rushcrew.order_service.infrastructure.persistence.outbox.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
	name = "p_outbox_event",
	schema = "order_schema",
	indexes = {
		@Index(name = "idx_status_created_at", columnList = "status, createdAt"),
		@Index(name = "idx_aggregate_id", columnList = "aggregateId")
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OutboxEventEntity {

	@Id
	private UUID eventId;

	@Column(nullable = false, length = 50)
	private String aggregateType;

	@Column(nullable = false)
	private UUID aggregateId;

	@Column(nullable = false, length = 100)
	private String eventType;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String payload;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OutboxStatus status;

	@Column(nullable = false)
	private Instant createdAt;

	private Instant publishedAt;

	private Instant failedAt;

	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	@Column(nullable = false)
	@Builder.Default
	private Integer retryCount = 0;

	public static OutboxEventEntity create(
		String aggregateType,
		UUID aggregateId,
		String eventType,
		String payload
	) {
		return OutboxEventEntity.builder()
			.eventId(UUID.randomUUID())
			.aggregateType(aggregateType)
			.aggregateId(aggregateId)
			.eventType(eventType)
			.payload(payload)
			.status(OutboxStatus.PENDING)
			.createdAt(Instant.now())
			.retryCount(0)
			.build();
	}

	public void markAsPublished() {
		this.status = OutboxStatus.PUBLISHED;
		this.publishedAt = Instant.now();
	}

	public void markAsFailed(String errorMessage) {
		this.status = OutboxStatus.FAILED;
		this.failedAt = Instant.now();
		this.errorMessage = errorMessage;
		this.retryCount++;
	}

	public boolean canRetry() {
		return this.retryCount < 3 && this.status == OutboxStatus.FAILED;
	}

	public void retry() {
		if (!canRetry()) {
			throw new IllegalStateException("재시도 불가능한 상태입니다.");
		}
		this.status = OutboxStatus.PENDING;
		this.errorMessage = null;
	}

	public enum OutboxStatus {
		PENDING,
		PUBLISHED,
		FAILED
	}
}
