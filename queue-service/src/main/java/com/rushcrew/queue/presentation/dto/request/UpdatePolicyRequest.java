package com.rushcrew.queue.presentation.dto.request;

import com.rushcrew.queue.application.command.UpdatePolicyCommand;
import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdatePolicyRequest(
    // TODO : 추후 추가 예정
//    Long userId,
//    UserRole role,
    UUID productId,
    String timeDealName,
    QueuePolicyStatus status,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Integer limitSize,
    Integer queueGap,
    Integer ttl
) {
    public UpdatePolicyCommand toCommand() {
        return UpdatePolicyCommand.builder()
            .productId(productId)
            .timeDealName(timeDealName)
            .status(status)
            .startTime(startTime)
            .endTime(endTime)
            .limitSize(limitSize)
            .queueGap(queueGap)
            .ttl(ttl)
            .build();
    }
}
