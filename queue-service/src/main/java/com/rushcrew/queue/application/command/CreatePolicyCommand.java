package com.rushcrew.queue.application.command;

import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import com.rushcrew.queue.domain.vo.TimePeriod;
import com.rushcrew.queue.domain.vo.TrafficSetting;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreatePolicyCommand(
    UUID productId,
    String dealName,
    QueuePolicyStatus status,
    TimePeriod timePeriod,
    TrafficSetting trafficSetting
) {

}
