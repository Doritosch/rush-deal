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
     * 1. 시작시간 <= 현재시간+1분 (1분 뒤에 시작할 정책까지 미리 조회해야 정각에 바로 실행 가능)
     * 2. 종료시간 > 현재시간
     * 3. 상태가 RUNNING인 경우
     *
     * TrafficSetting 정보(QueueGap, LimitSize 등)는 스케줄러에서 매번 접근
     * Fetch Join을 사용하여 N+1 문제 없이 한 번의 쿼리로 로딩해옴
     */
    @Query("SELECT p from QueuePolicy p " +
        "JOIN FETCH p.trafficSetting " +
    "WHERE p.status = 'RUNNING' " +
    "AND p.timePeriod.endTime > :now " +
    "AND p.timePeriod.startTime <= :nowPlus1Min")
    List<QueuePolicy> findAllActivePolicies(@Param("now") LocalDateTime now, @Param("nowPlus1Min") LocalDateTime nowPlus1Min);
}
