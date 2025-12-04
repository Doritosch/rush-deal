package com.rushcrew.queue.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record EnterQueueRequest(
    @NotBlank(message = "상품 ID는 필수입니다")
    UUID productId
) {
}
