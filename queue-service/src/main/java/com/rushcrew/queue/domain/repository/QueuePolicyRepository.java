package com.rushcrew.queue.domain.repository;

import com.rushcrew.queue.domain.entity.QueuePolicy;
import java.util.Optional;
import java.util.UUID;

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
}
