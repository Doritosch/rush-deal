package com.rushcrew.queue.presentation.dto.response;

import com.rushcrew.queue.domain.enums.QueueStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record QueueResponse(
    UUID token,
    UUID productId,
    Long rank,
    String status,
    LocalDateTime enteredAt, // 대기열 진입 요청 시간
    String message
) {

}
