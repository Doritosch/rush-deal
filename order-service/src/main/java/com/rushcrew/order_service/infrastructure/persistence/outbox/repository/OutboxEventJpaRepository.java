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
	 * 
	 * 주의: 동시성 제어를 위해 FOR UPDATE SKIP LOCKED를 사용하는 것을 권장한다
	 * 현재는 기본 조회만 제공하며, 동시성 문제가 발생할 수 있다
	 */
	@Query("SELECT o FROM OutboxEventEntity o " +
		"WHERE o.status = 'PENDING' " +
		"ORDER BY o.createdAt ASC")
	List<OutboxEventEntity> findPendingEvents(Pageable pageable);

	/**
	 * PENDING 상태의 이벤트 조회 (동시성 제어 포함)
	 * FOR UPDATE SKIP LOCKED를 사용하여 여러 인스턴스가 동시에 실행해도
	 * 같은 이벤트를 중복 처리하지 않도록 보장
	 * 
	 * @param limit 조회할 최대 이벤트 수
	 * @return 처리 가능한 PENDING 이벤트 목록
	 */
	@Query(value = "SELECT * FROM p_outbox_event o " +
		"WHERE o.status = 'PENDING' " +
		"ORDER BY o.created_at ASC " +
		"LIMIT :limit " +
		"FOR UPDATE SKIP LOCKED", nativeQuery = true)
	List<OutboxEventEntity> findPendingEventsForUpdate(@Param("limit") int limit);


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
