package com.rushcrew.queue.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrafficSetting {
    // 활성 허용 인원
    @Column(name = "limit_size", nullable = false)
    private Integer limitSize;

    // 활성 체크 주기 (몇 초 마다 확인해서 들여보낼 지 주기)
    @Column(name = "queue_gap", nullable = false)
    private Integer queueGap;

    // 토큰 유효 시 (Active 토큰의 만료 시간 (예: 300초))
    @Column(name = "ttl", nullable = false)
    private Integer ttl;

    public TrafficSetting(Integer limitSize, Integer queueGap, Integer ttl) {
        if (limitSize == null || limitSize <= 0) {
            throw new IllegalArgumentException("진입 허용 인원은 1명 이상이어야 합니다.");
        }

        if (limitSize > 10000) {
            throw new IllegalArgumentException("진입 허용 인원은 10,000명을 초과할 수 없습니다.");
        }

        if (queueGap == null || queueGap <= 0) {
            throw new IllegalArgumentException("활성 체크 주기는 1초 이상이어야 합니다.");
        }

        if (ttl == null || ttl <= 60) {
            throw new IllegalArgumentException("TTL은 최소 60초 이상이어야 합니다.");
        }
        this.limitSize = limitSize;
        this.queueGap = queueGap;
        this.ttl = ttl;
    }
}
