package com.rushcrew.queue.domain.repository;

import com.rushcrew.queue.domain.entity.QueuePolicy;
import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueuePolicyRepository {

    /**
     * 타임딜 정책 등록
     * @param queuePolicy
     * @return
     */
    QueuePolicy save(QueuePolicy queuePolicy);

    /**
     * 정책 ID로 조회
     * @param policyId
     * @return
     */
    Optional<QueuePolicy> findById(UUID policyId);

    /**
     * 상품 ID로 타임딜 정책 조회
     * @param productId
     * @return
     */
    Optional<QueuePolicy> findByProductId(UUID productId);

    /**
     * 타임딜 정책 목록 페이징 조회
     * 동적 검색 (상품ID, 상태가 null이면 전체 조회, 있으면 필터링)
     * @param productId
     * @param status
     * @param pageable
     * @return
     */
    Page<QueuePolicy> findAllByCondition(UUID productId, QueuePolicyStatus status, Pageable pageable);
}
