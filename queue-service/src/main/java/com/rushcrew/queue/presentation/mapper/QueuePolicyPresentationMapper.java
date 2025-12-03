package com.rushcrew.queue.presentation.mapper;

import com.rushcrew.queue.application.dto.QueuePolicyQueryResponse;
import com.rushcrew.queue.domain.entity.QueuePolicy;
import com.rushcrew.queue.presentation.dto.response.CreatePolicyResponse;
import com.rushcrew.queue.presentation.dto.response.QueuePolicyResponse;
import org.springframework.stereotype.Component;

/**
 * Application DTO -> Presentation DTO
 */
@Component
public class QueuePolicyPresentationMapper {
    public CreatePolicyResponse toCreatePolicyResponse(QueuePolicyQueryResponse response) {
        return new CreatePolicyResponse(
            response.policyId(),
            response.productId(),
            response.timeDealName()
        );
    }

    public QueuePolicyResponse toQueuePolicyResponse(QueuePolicyQueryResponse response) {
        return new QueuePolicyResponse(
            response.policyId(),
            response.productId(),
            response.timeDealName(),
            response.status(),
            response.startTime(),
            response.endTime(),
            response.limitSize(),
            response.queueGap(),
            response.ttl()
        );
    }
}
