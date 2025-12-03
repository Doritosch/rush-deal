package com.rushcrew.queue.application.command;

import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import java.util.UUID;

public record SearchPolicyCommand(
    UUID productId,
    String status
) {
    public static SearchPolicyCommand of(UUID productId, String status) {
        if (status == null) {
            return new SearchPolicyCommand(productId, null);
        }
        if (productId == null) {
            return new SearchPolicyCommand(null, status);
        }
        return new SearchPolicyCommand(productId, status);
    }

    public QueuePolicyStatus getQueuePolicyStatus() {
        if (this.status == null || this.status.isBlank()) {
            return null; // null 또는 공백이면 null 반환
        }
        try {
            return QueuePolicyStatus.valueOf(this.status.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 값은 여전히 비즈니스 예외 처리
            throw new IllegalArgumentException("유효하지 않은 정책 상태 값입니다: " + this.status);
        }
    }
}
