package com.rushcrew.queue.application.dto;

import com.rushcrew.queue.domain.entity.QueuePolicy;
import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record QueuePolicyResponse(
    UUID policyId,
    UUID productId,
    String timeDealName,
    QueuePolicyStatus status,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Integer limitSize,
    Integer queueGap,
    Integer ttl
) {
    public static QueuePolicyResponse from(QueuePolicy queuePolicy) {
        return QueuePolicyResponse.builder()
            .policyId(queuePolicy.getPolicyId())
            .productId(queuePolicy.getProductId())
            .timeDealName(queuePolicy.getTimeDealName())
            .status(queuePolicy.getStatus())
            .startTime(queuePolicy.getTimePeriod().getStartTime())
            .endTime(queuePolicy.getTimePeriod().getEndTime())
            .limitSize(queuePolicy.getTrafficSetting().getLimitSize())
            .queueGap(queuePolicy.getTrafficSetting().getQueueGap())
            .ttl(queuePolicy.getTrafficSetting().getTtl())
            .build();
    }
}
