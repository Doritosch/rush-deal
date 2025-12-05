package com.rushcrew.queue.infrastructure.repository.jpa;

import com.rushcrew.queue.domain.entity.QueuePolicy;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaQueuePolicyRepository extends JpaRepository<QueuePolicy, UUID> {
    Optional<QueuePolicy> findByPolicyIdAndDeletedAtIsNull(UUID policyId);

    Optional<QueuePolicy> findByProductIdAndDeletedAtIsNull(UUID productId);
}
