package com.rushcrew.queue.infrastructure.repository.jpa;

import com.rushcrew.queue.domain.entity.QueuePolicy;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JpaQueuePolicyRepository extends JpaRepository<QueuePolicy, UUID>, JpaSpecificationExecutor<QueuePolicy> {
    Optional<QueuePolicy> findByPolicyIdAndDeletedAtIsNull(UUID policyId);

    Optional<QueuePolicy> findByProductIdAndDeletedAtIsNull(UUID productId);
}
