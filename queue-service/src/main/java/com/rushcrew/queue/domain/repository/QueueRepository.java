package com.rushcrew.queue.domain.repository;

import com.rushcrew.queue.domain.entity.QueueToken;
import java.util.List;
import java.util.UUID;

public interface QueueRepository {
    // 대기열 등록 (ZSet add)
    void register(QueueToken token);

    // 토큰 활성화 (대기열 -> 활성열)
    void activateTokens(Long productId, List<String> tokens);

    // 활성 토큰 검증 (주문 서비스에서 검증 요청 시 사용)
    boolean isActivatedToken(Long productId, QueueToken token);

    // 토큰 상태/대기 순번 확인 (ZSet rank -> polling)
    Long getWaitingRank(UUID productId, QueueToken token);
}
