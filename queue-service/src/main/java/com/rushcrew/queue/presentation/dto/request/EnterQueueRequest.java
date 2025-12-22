package com.rushcrew.queue.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record EnterQueueRequest(
    @NotNull(message = "상품 ID는 필수입니다")
    UUID productId
) {
}
