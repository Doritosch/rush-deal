package com.rushcrew.queue.infrastructure.repository.jpa;

import com.rushcrew.queue.domain.entity.QueuePolicy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaQueuePolicyRepository extends JpaRepository<QueuePolicy, UUID>, JpaSpecificationExecutor<QueuePolicy> {
    Optional<QueuePolicy> findByPolicyIdAndDeletedAtIsNull(UUID policyId);

    Optional<QueuePolicy> findByProductIdAndDeletedAtIsNull(UUID productId);

    /**
     * 현재 활성화된(진행 중인) 타임딜 정책 조회
     * 조건: 시작시간 <= 현재시간 && 종료시간 > 현재시간
     */

    /**
     * 현재 활성화된(진행 중인) 타임딜 정책 조회
     * 1. 시작시간 <= 현재시간
     * 2. 종료시간 > 현재시간
     * 3. 상태가 RUNNING인 경우
     */
    @Query("SELECT p from QueuePolicy p " +
    "WHERE p.timePeriod.startTime <= :now " +
    "AND p.timePeriod.endTime > :now " +
    "AND p.status = 'RUNNING' ")
    List<QueuePolicy> findAllActivePolicies(@Param("now") LocalDateTime now);
}
