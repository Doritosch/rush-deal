package com.rushcrew.queue.infrastructure.repository;

import com.rushcrew.queue.domain.entity.QueuePolicy;
import com.rushcrew.queue.domain.repository.QueuePolicyRepository;
import com.rushcrew.queue.infrastructure.repository.jpa.JpaQueuePolicyRepository;
import java.util.Optional;
import java.util.UUID;
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
}
