package com.rushcrew.queue.application.port.in;

import com.rushcrew.queue.application.command.queue.EnterQueueCommand;
import com.rushcrew.queue.application.dto.QueueRedisResponse;
import com.rushcrew.queue.domain.vo.TokenId;
import com.rushcrew.queue.domain.vo.TrafficSetting;
import java.util.List;
import java.util.UUID;

public interface QueuePort {
    /**
     * 대기열 진입 (토큰 발급)
     */
    QueueRedisResponse enterQueue(EnterQueueCommand command);

    /**
     * 대기 상태 조회 (Polling)
     */
    QueueRedisResponse getQueueRank(UUID productId, String token, Long userId, String role);

    /**
     * 토큰 형식 검증
     */
    TokenId validateQueueToken(String token);

    /**
     * 토큰 형식 검증 + 유저 토큰 활성화 여부
     */
    boolean validateActivatedQueueToken(UUID productId, String token);

    /**
     * 토큰 활성화
     * 스케줄러가 호출: 대기 -> 활성 전환
     */
    void activateTokens(UUID productId, TrafficSetting trafficSetting);
}
