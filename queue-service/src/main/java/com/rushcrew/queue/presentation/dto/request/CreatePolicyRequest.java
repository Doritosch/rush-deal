package com.rushcrew.queue.presentation.dto.request;

import com.rushcrew.queue.application.command.CreatePolicyCommand;
import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreatePolicyRequest(
    // TODO : 추후 추가 예정
//    Long userId,
//    UserRole role,
    @NotBlank(message = "상품 ID는 필수입니다")
    UUID productId,
    @NotBlank(message = "타임딜 제목은 필수입니다")
    String dealName,
    @NotBlank(message = "초기 상태값은 필수입니다")
    QueuePolicyStatus status,
    @NotBlank(message = "시작 시간은 필수입니다")
    LocalDateTime startTime,
    @NotBlank(message = "종료 시간은 필수입니다")
    LocalDateTime endTime,
    @NotBlank(message = "허용 인원은 필수입니다")
    @Min(value = 1, message = "허용 인원은 최소 1명 이상이어야 합니다")
    Integer limitSize,
    @NotBlank(message = "활성 체크 주기는 필수입니다")
    @Min(value = 1, message = "활성 체크 주기는 최소 1 이상이어야 합니다")
    Integer queueGap,
    @NotBlank(message = "TTL은 필수입니다")
    @Min(value = 60, message = "TTL은 최소 60초 이상이어야 합니다")
    Integer ttl
) {
    public CreatePolicyCommand toCommand() {
        return CreatePolicyCommand.builder()
            .productId(productId)
            .dealName(dealName)
            .status(status)
            .startTime(startTime)
            .endTime(endTime)
            .limitSize(limitSize)
            .queueGap(queueGap)
            .ttl(ttl)
            .build();
    }
}
