package com.rushcrew.queue.presentation.dto.response;

import com.rushcrew.queue.domain.entity.QueuePolicy;
import java.util.UUID;

public record CreatePolicyResponse(
    UUID policyId,
    UUID productId,
    String dealName
) {
    public static CreatePolicyResponse from(QueuePolicy policy) {
        return new CreatePolicyResponse(
            policy.getPolicyId(),
            policy.getProductId(),
            policy.getTimeDealName()
        );
    }
}
