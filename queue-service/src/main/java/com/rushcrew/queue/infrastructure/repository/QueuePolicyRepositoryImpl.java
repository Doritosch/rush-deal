package com.rushcrew.queue.infrastructure.repository;

import com.rushcrew.queue.domain.entity.QueuePolicy;
import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import com.rushcrew.queue.domain.repository.QueuePolicyRepository;
import com.rushcrew.queue.infrastructure.repository.jpa.JpaQueuePolicyRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class QueuePolicyRepositoryImpl implements QueuePolicyRepository {

    private final JpaQueuePolicyRepository jpaQueuePolicyRepository;

    public QueuePolicyRepositoryImpl(JpaQueuePolicyRepository jpaQueuePolicyRepository) {
        this.jpaQueuePolicyRepository = jpaQueuePolicyRepository;
    }

    @Override
    public QueuePolicy save(QueuePolicy queuePolicy) {
        return jpaQueuePolicyRepository.save(queuePolicy);
    }

    @Override
    public Optional<QueuePolicy> findById(UUID policyId) {
        return jpaQueuePolicyRepository.findByPolicyIdAndDeletedAtIsNull(policyId);
    }

    @Override
    public Optional<QueuePolicy> findByProductId(UUID productId) {
        return jpaQueuePolicyRepository.findByProductIdAndDeletedAtIsNull(productId);
    }

    /**
     * 타임딜 정책 페이징 목록 조회 (동적 쿼리 검색)
     */
    @Override
    public Page<QueuePolicy> findAllByCondition(UUID productId, QueuePolicyStatus status, Pageable pageable) {
        // 동적 쿼리
        Specification<QueuePolicy> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // productId가 있으면 조건 추가
            if (productId != null) {
                predicates.add(cb.equal(root.get("productId"), productId));
            }

            // status가 있으면 조건 추가
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 삭제되지 않은 데이터만 조회
            predicates.add(cb.isNull(root.get("deletedAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return jpaQueuePolicyRepository.findAll(spec, pageable);
    }
}
