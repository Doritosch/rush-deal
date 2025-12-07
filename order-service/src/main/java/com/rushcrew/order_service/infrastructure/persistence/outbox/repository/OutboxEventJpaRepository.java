package com.rushcrew.order_service.infrastructure.persistence.outbox.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rushcrew.order_service.infrastructure.persistence.outbox.entity.OutboxEventEntity;
import com.rushcrew.order_service.infrastructure.persistence.outbox.entity.OutboxEventEntity.OutboxStatus;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, UUID> {

	/**
	 * PENDING 상태의 이벤트 조회 (Polling용)
	 * LIMIT 100 → Pageable 사용
	 */
	@Query("SELECT o FROM OutboxEventEntity o " +
		"WHERE o.status = 'PENDING' " +
		"ORDER BY o.createdAt ASC")
	List<OutboxEventEntity> findPendingEvents(Pageable pageable);


	/**
	 * 재시도 대상 FAILED 이벤트 조회
	 * LIMIT 50 → Pageable 사용
	 */
	@Query("SELECT o FROM OutboxEventEntity o " +
		"WHERE o.status = 'FAILED' " +
		"AND o.retryCount < 3 " +
		"AND o.failedAt < :oneHourAgo " +
		"ORDER BY o.failedAt ASC")
	List<OutboxEventEntity> findFailedEventsForRetry(
		@Param("oneHourAgo") Instant oneHourAgo,
		Pageable pageable
	);


	/**
	 * 오래된 PUBLISHED 이벤트 삭제 (정리용)
	 */
	@Modifying
	@Query("DELETE FROM OutboxEventEntity o " +
		"WHERE o.status = 'PUBLISHED' " +
		"AND o.publishedAt < :before")
	int deletePublishedEventsBefore(@Param("before") Instant before);


	/**
	 * 특정 Aggregate의 이벤트 조회
	 */
	List<OutboxEventEntity> findByAggregateIdOrderByCreatedAtDesc(UUID aggregateId);


	/**
	 * 상태 카운트 메트릭용
	 * (예: countByStatus(FAILED))
	 */
	long countByStatus(OutboxStatus status);
}
