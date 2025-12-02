package com.rushcrew.queue.presentation.dto.request;

import com.rushcrew.queue.application.command.CreatePolicyCommand;
import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import com.rushcrew.queue.domain.vo.TimePeriod;
import com.rushcrew.queue.domain.vo.TrafficSetting;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreatePolicyRequest(
    @NotNull(message = "상품 ID는 필수입니다")
    UUID productId,
    @NotBlank(message = "타임딜 제목은 필수입니다")
    String dealName,
    @NotNull(message = "초기 상태값은 필수입니다")
    QueuePolicyStatus status,
    @NotNull(message = "시작 시간은 필수입니다")
    LocalDateTime startTime,
    @NotNull(message = "종료 시간은 필수입니다")
    LocalDateTime endTime,
    @NotNull(message = "허용 인원은 필수입니다")
    @Min(value = 1, message = "허용 인원은 최소 1명 이상이어야 합니다")
    Integer limitSize,
    @NotNull(message = "활성 체크 주기는 필수입니다")
    @Min(value = 1, message = "활성 체크 주기는 최소 1 이상이어야 합니다")
    Integer queueGap,
    @NotNull(message = "TTL은 필수입니다")
    @Min(value = 60, message = "TTL은 최소 60초 이상이어야 합니다")
    Integer ttl
) {
    public CreatePolicyCommand toCommand() {
        // vo 생성 시 유효성 검증
        TimePeriod timePeriod = new TimePeriod(startTime, endTime);
        TrafficSetting traffic = new TrafficSetting(limitSize, queueGap, ttl);

        return CreatePolicyCommand.builder()
            .productId(productId)
            .dealName(dealName)
            .status(status)
            .timePeriod(timePeriod)
            .trafficSetting(traffic)
            .build();
    }
}
