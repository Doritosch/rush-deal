package com.rushcrew.queue.domain.entity;

import com.rushcrew.queue.domain.enums.QueueStatus;
import com.rushcrew.queue.domain.vo.TokenId;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * 실질적 Aggregate Root가 됨
 * (엔티티 어노테이션이 없는 순수 자바 객체이나 생명주기가 있고, DDD상의 개념은 엔티티로 들어감)
 */
@Getter
@Builder
public class QueueToken {
    private final TokenId id; // Redis 토큰 발급
    private Long userId;
    private UUID productId;
    private QueueStatus status;
    private Long requestTime; // 대기열 진입 요청 시간 (Score용)
    private Long activeTime;  // 활성화 시간 (TTL 계산용)

    // 대기 토큰 생성
    public static QueueToken create(UUID productId, Long userId) {
        return QueueToken.builder()
            .id(TokenId.generate())
            .productId(productId)
            .userId(userId)
            .status(QueueStatus.WAITING) // 처음 생성 시 기본값 WAITING
            .requestTime(System.currentTimeMillis())
            .build();
    }

    // 토큰 활성화 (WAIT -> ACTIVE 상태 변경)
    public void activate() {
        if (this.status != QueueStatus.WAITING) {
            throw new IllegalStateException("대기 상태의 토큰만 활성화할 수 있습니다.");
        }
        this.status = QueueStatus.ACTIVE;
        this.activeTime = System.currentTimeMillis();
    }

    // 토큰 만료
    public void expire() {
        this.status = QueueStatus.EXPIRED;
    }

    // 활성화 여부 확인
    public boolean isActive() {
        return this.status == QueueStatus.ACTIVE;
    }
}
