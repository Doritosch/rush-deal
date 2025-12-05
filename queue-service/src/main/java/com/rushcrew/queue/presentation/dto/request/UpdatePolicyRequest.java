package com.rushcrew.queue.presentation.dto.request;

import com.rushcrew.queue.application.command.UpdatePolicyCommand;
import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import com.rushcrew.queue.domain.vo.TimePeriod;
import com.rushcrew.queue.domain.vo.TrafficSetting;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdatePolicyRequest(
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
        // vo 생성 시 유효성 검증
        TimePeriod timePeriod = new TimePeriod(startTime, endTime);
        TrafficSetting traffic = new TrafficSetting(limitSize, queueGap, ttl);

        return UpdatePolicyCommand.builder()
            .productId(productId)
            .timeDealName(timeDealName)
            .status(status)
            .timePeriod(timePeriod)
            .trafficSetting(traffic)
            .build();
    }
}
