package com.rushcrew.queue.domain.enums;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.common.global.error.CommonErrorCode;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum QueueStatus {
    WAITING("대기"),   // 대기열 진입 (Redis Sorted Set)
    ACTIVE("입장가능"), // 입장 가능 (Set)
    EXPIRED("만료"); // 만료

    private final String description;

    QueueStatus(String description) {
        this.description = description;
    }

    public static QueueStatus of(String status) {
        return Arrays.stream(QueueStatus.values())
            .filter(s -> s.name().equalsIgnoreCase(status))
            .findFirst()
            .orElseThrow(() -> new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }
}
