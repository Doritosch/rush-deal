package com.rushcrew.queue.infrastructure.repository;

import com.rushcrew.queue.domain.entity.QueueToken;
import com.rushcrew.queue.domain.repository.QueueRepository;
import com.rushcrew.queue.domain.vo.TokenId;
import com.rushcrew.queue.domain.vo.TrafficSetting;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class RedisQueueRepository implements QueueRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String WAITING_KEY = "queue:wait:product:%s";
    private static final String ACTIVE_KEY = "queue:active:product:%s";
    private static final String USER_INDEX_KEY = "queue:user:product:%s:%s"; // String (중복방지용)

    // FAST TRACK(대기열 진입 정책) 기준 인원 (100인 미만이면 대기열 토큰 생성 시 바로 활성열로 이동)
    // TODO: 추후 QueuePolicy (정책 DB)에서 관리하도록 수정 예정
    private static final Long MAX_ACTIVE_COUNT = 100L;

    public RedisQueueRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 대기열 등록 (ZSet : Sorted Set)
     */
    @Override
    public boolean register(QueueToken token, LocalDateTime dealEndTime, Integer activeTtl) {
        // TTL 계산 : (이벤트 종료 시간 - 현재 시간)
        long secondsUntilClose = Duration.between(LocalDateTime.now(), dealEndTime).getSeconds();

        if (secondsUntilClose < 0) {
            // 이미 종료된 이벤트면 진입 불가 처리
            log.warn("[QUEUE:ERROR] 이미 종료된 이벤트입니다. productId={}", token.getProductId());
            return false;
        }

        // redis 사용자 인덱스 키 생성
        String userIndexKey = getUserIndexKey(token.getProductId(), token.getUserId());

        // 중복 방지 : 유저별 대기열 키 생성 (SETNX)
        // KEY: queue:user:product:{productId}:{userId} / VALUE: 토큰 UUID
        Boolean isNewUser = redisTemplate.opsForValue().setIfAbsent(
            userIndexKey,
            token.getId().getValue().toString(),
            Duration.ofMinutes(secondsUntilClose) // TTL 설정: 타임딜 종료 시간에 맞춰 자동 만료
        );

        if (Objects.equals(isNewUser, Boolean.FALSE)) {
            // 이미 대기 중인 유저
            log.warn("[QUEUE:ERROR] 이미 대기 중인 사용자입니다. userId={}", token.getUserId());
            return false;
        }

        // FAST TRACK 판단 : 활성열 인원 조회
        Long activeCount = countActiveTokens(token.getProductId());
        if (activeCount != null && activeCount < MAX_ACTIVE_COUNT) {
            // [Fast Track] 대기 없이 바로 활성 상태 진입
            return registerFastTrack(token, dealEndTime, activeTtl, userIndexKey);
        } else {
            // 대기열 등록 (ZSet)
            return registerWaitingQueue(token, userIndexKey);
        }
    }

    @Override
    public void activateTokens(UUID productId, List<String> tokens, TrafficSetting setting) {
        if (tokens == null || tokens.isEmpty()) { return; }

        String waitingKey = getWaitingKey(productId);
        String activeKey = getActiveKey(productId);

        // TrafficSetting의 TTL을 사용하여 만료 시간 계산
        double expireAt = getExpireAt(setting.getTtl());

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            // StringRedisConnection으로 형변환
            StringRedisConnection strConnection = (StringRedisConnection) connection;
                for (String token : tokens) {
                    // 활성열 추가 (Score = 만료 예정 시간)
                    strConnection.zAdd(activeKey, expireAt, token);

                    // 대기열 제거
                    strConnection.zRem(waitingKey, token) ;
                }
                return null; // 파이프라인은 반환값이 null이어야 함
        });
        log.info("[QUEUE:SUCCESS:ACTIVE] 상품({}) :: {}명 활성화 완료 (Max: {}, Limit: {})",
            productId, tokens.size(), setting.getMaxCapacity(), setting.getLimitSize());
    }

    /**
     * 활성 대상 토큰 조회
     * ZSet에서 점수가 가장 낮은 N명 조회
     */
    @Override
    public List<String> getWaitingTokens(UUID productId, long count) {
        // Score(타임스탬프)가 낮은 순서(진입 요청 시점이 이른 것부터)대로 조회 (FIFO)
        // 0번부터 count-1명까지 (상위 N명) -> Redis ZRANGE key 0 N (Score가 가장 낮은 순서부터 N개 가져옴)
        Set<String> tokens = redisTemplate.opsForZSet().range(
            getWaitingKey(productId),
            0,
            count - 1
        );
        return tokens == null ? List.of() : new ArrayList<>(tokens);
    }

    @Override
    public boolean isActivatedToken(UUID productId, TokenId tokenId) {
        // ZSet에서 Score(만료시간) 조회
        String activeKey = getActiveKey(productId);
        Double expireTime = redisTemplate.opsForZSet().score(activeKey, tokenId.getValue());

        // 활성열에 없음
        if (expireTime == null) return false;

        // 만료 시간 지났는지 확인 (Lazy Validation)
        long now = System.currentTimeMillis();
        if (expireTime < now) {
            // 만료되었으면 삭제
            redisTemplate.opsForZSet().remove(
                activeKey,
                tokenId.getValue()
            );
            log.info("[QUEUE:EXPIRE:ACTIVE] 활성 토큰 만료됨. token={}", tokenId);
            return false;
        }
        return true;
    }

    /**
     * 활성 토큰 수 확인 (ZSet Size 조회)
     * 조회 직전에 이미 만료된 토큰을 삭제하여 정확한 수를 반환함 (Lazy cleanup)
     */
    @Override
    public Long countActiveTokens(UUID productId) {
        String activeKey = getActiveKey(productId);

        // lazy cleanup
        // ZSet의 Score(만료시간)가 현재 시간보다 작은(과거인) 멤버들 삭제
        // ZREMRANGEBYSCORE key -inf current_timestamp
        double now = System.currentTimeMillis() / 1000.0;
        redisTemplate.opsForZSet().removeRangeByScore(
            activeKey,
            Double.NEGATIVE_INFINITY,
            now
        );

        // 청소 후 남은 개수 반환
        Long count = redisTemplate.opsForZSet().zCard(activeKey);
        return count != null ? count : 0L;
    }

    @Override
    public Long getWaitingRank(UUID productId, TokenId tokenId) {
        // ZRANK key member (ZSet 조회)
        // Redis ZRANK 통하여 대기 순번 조회 성능을 O(log N)으로 최적화
        return redisTemplate.opsForZSet()
            .rank(getWaitingKey(productId),
                tokenId.getValue().toString()
            );
    }

    @Override
    public Double getWaitingScore(UUID productId, TokenId tokenId) {
        // Redis ZSCORE로 진입 시간 조회
        return redisTemplate.opsForZSet()
            .score(getWaitingKey(productId),
                tokenId.getValue().toString()
            );
    }

    /**
     * TODO: Order Service 쪽에서 결제/주문 로직이나, 트랜잭션 종료 시점에 해당 API를 호출하여 토큰을 정리해야 함
     * @param productId
     * @param tokenId
     */
    @Override
    public void removeToken(UUID productId, TokenId tokenId) {
        // 활성열(ZSet)에서 해당 토큰 삭제
        redisTemplate.opsForZSet()
            .remove(
                getActiveKey(productId),
                tokenId.getValue().toString()
            );
    }

    /**
     * 본인 확인 (대기열 토큰 소유권 검증)
     */
    @Override
    public boolean verifyTokenOwner(UUID productId, Long userId, String token) {
        // redis에 저장된 해당 유저 토큰 조회
        String savedToken = redisTemplate.opsForValue().get(
            getUserIndexKey(productId, userId)
        );

        // 저장된 토큰 없거나, 요청 토큰과 다르면 본인 아님
        return savedToken != null && savedToken.equals(token);
    }

    /**
     * Fast Track: 즉시 활성열 등록 (ZSet 등록)
     */
    private boolean registerFastTrack(QueueToken token, LocalDateTime dealEndTime, Integer activeTtl,
        String userIndexKey) {
        // ActiveKey(활성열 키) 생성
        String activeKey = getActiveKey(token.getProductId());
        try {
            // 만료 시간(score) 계산: 현재시간 기준 + activeTtl
            double expireAt = getExpireAt(activeTtl);

            // active(활성열) ZSet에 저장 (Score = 만료시간)
            redisTemplate.opsForZSet().add(
                activeKey,
                token.getId().getValue().toString(),
                expireAt
            );
            log.info("[QUEUE:REDIS] FAST TRACK 활성열 등록 성공: user={}, token={}", token.getUserId(), token.getId());
            return true;
        } catch (Exception e) {
            // 롤백: 문제 발생 시 유저 인덱스 삭제 (재진입 허용)
            // 보상 트랜잭션 : Set 저장 실패 시, 중복 방지 키(userIndexKey)와 activeKey도 삭제해줘야 유저가 다시 시도 가능
            log.error("[QUEUE:REDIS:ERROR] FAST TRACK 활성열 진입 실패로 인한 롤백 수행: userId={}, tokenId={}", token.getUserId(), token.getId());
            redisTemplate.delete(userIndexKey);
            redisTemplate.opsForSet().remove(activeKey, token.getId().toString());
            throw e;
        }
    }

    /**
     * 대기열 등록 (ZSet 등록)
     */
    private boolean registerWaitingQueue(QueueToken token, String userIndexKey) {
        try {
            // TODO: 추후 확인 -> System.currentTimeMillis()는 동시성 이슈가 미세하게 있을 수 있으므로 nanoTime 혼용 추천
            double score = System.currentTimeMillis();
            redisTemplate.opsForZSet().add(
                getWaitingKey(token.getProductId()),
                token.getId().getValue().toString(),
                score
            );
            log.info("[QUEUE:REDIS] 대기열 진입 성공: user={}, score={}", token.getUserId(), score);
            return true;
        } catch (Exception e) {
            // 보상 트랜잭션 : ZSet 저장 실패 시, 중복 방지 키(userIndexKey)도 삭제해줘야 유저가 다시 시도 가능
            log.error("[QUEUE:REDIS:ERROR] 대기열 진입 실패로 인한 롤백 수행: userId={}, tokenId={}", token.getUserId(), token.getId());
            redisTemplate.delete(userIndexKey);
            throw e;
        }
    }

    private String getWaitingKey(UUID productId) {
        return String.format(WAITING_KEY, productId);
    }

    private String getActiveKey(UUID productId) {
        return String.format(ACTIVE_KEY, productId);
    }

    private String getUserIndexKey(UUID productId, Long userId) {
        return String.format(USER_INDEX_KEY, productId, userId);
    }

    private double getExpireAt(Integer activeTtl) {
        long now = System.currentTimeMillis();
        return now + (activeTtl * 1000L);
    }
}
