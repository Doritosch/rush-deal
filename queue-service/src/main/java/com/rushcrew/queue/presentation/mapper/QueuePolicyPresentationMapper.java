package com.rushcrew.queue.presentation.mapper;

import com.rushcrew.queue.application.dto.QueuePolicyResponse;
import com.rushcrew.queue.presentation.dto.response.CreatePolicyResponse;
import org.springframework.stereotype.Component;

@Component
public class QueuePolicyPresentationMapper {
    public CreatePolicyResponse toCreatePolicyResponse(QueuePolicyResponse response) {
        return new CreatePolicyResponse(
            response.policyId(),
            response.productId(),
            response.timeDealName()
        );
    }
}
