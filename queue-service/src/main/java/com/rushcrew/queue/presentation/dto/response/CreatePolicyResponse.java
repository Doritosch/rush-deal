package com.rushcrew.queue.presentation.dto.response;

import java.util.UUID;

public record CreatePolicyResponse(
    UUID policyId,
    UUID productId,
    String dealName
) {
}
