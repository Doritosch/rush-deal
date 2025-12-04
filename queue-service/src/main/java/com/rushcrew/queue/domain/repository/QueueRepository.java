package com.rushcrew.queue.domain.repository;

import com.rushcrew.queue.domain.entity.QueueToken;
import com.rushcrew.queue.domain.vo.TokenId;
import java.util.List;
import java.util.UUID;

public interface QueueRepository {
    /**
     * Redis의 대기열에 Sorted Set (ZSet) 타입으로 저장
     * Score에 타임스탬프를 사용하는 구조 (선착순 진입 순서 보장)
     * @param token
     */
    boolean register(QueueToken token);

    /**
     * 토큰 활성화 (대기열 -> 활성열)
     * 스케줄러용: 대기열에서 N명을 활성열로 이동
     * @param productId
     * @param tokens
     */
    void activateTokens(UUID productId, List<String> tokens);

    /**
     * 활성 토큰 검증 (주문 서비스에서 검증 요청 시 사용)
     * SET ISMEMBER
     * @param productId
     * @param tokenId
     * @return
     */
    boolean isActivatedToken(UUID productId, TokenId tokenId);

    /**
     * 토큰 상태/대기 순번 확인 (ZSet rank -> polling)
     * (ZSet 조회 - O(logN))
     * @param productId
     * @param tokenId
     * @return
     */
    Long getWaitingRank(UUID productId, TokenId tokenId);

    /**
     * 토큰 Score 조회 (요청 시간)
     * Redis ZSCORE로 진입 시간 조회
     * @param productId
     * @param tokenId
     * @return
     */
    Double getWaitingScore(UUID productId, TokenId tokenId);
}
