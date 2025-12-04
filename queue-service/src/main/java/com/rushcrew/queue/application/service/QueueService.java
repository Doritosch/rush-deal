package com.rushcrew.queue.application.service;

import com.rushcrew.queue.application.command.queue.EnterQueueCommand;
import com.rushcrew.queue.application.dto.QueueRedisResponse;
import com.rushcrew.queue.application.port.in.QueuePort;
import com.rushcrew.queue.domain.entity.QueuePolicy;
import com.rushcrew.queue.domain.entity.QueueToken;
import com.rushcrew.queue.domain.enums.QueueStatus;
import com.rushcrew.queue.domain.repository.QueuePolicyRepository;
import com.rushcrew.queue.domain.repository.QueueRepository;
import com.rushcrew.queue.domain.vo.TokenId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QueueService implements QueuePort {
    private final QueueRepository queueRepository;
    private final QueuePolicyRepository queuePolicyRepository;

    public QueueService(QueueRepository queueRepository, QueuePolicyRepository queuePolicyRepository) {
        this.queueRepository = queueRepository;
        this.queuePolicyRepository = queuePolicyRepository;
    }

    /**
     * 대기열 진입
     * TODO: product ID는 타임딜 정책(QueuePolicy) 테이블에서 유효성 검증
     * 관리자가 정책을 등록하지 않은 상품은 대기열을 생성할 수 없음
     */
    @Override
    @Transactional(readOnly = true)
    public QueueRedisResponse enterQueue(EnterQueueCommand command) {
        // 대기열 정책 확인 (RDB 조회 - 상품 존재 여부 및 시간 확인)
        QueuePolicy policy = queuePolicyRepository.findByProductId(command.productId())
            .orElseThrow(() -> new NoSuchElementException("타임딜이 운영되지 않는 상품입니다."));

        // TODO: 대기열 정책에서 대기열 진입 시간 확인 로직 추가 필요
//        if (policy.isOpen()) {}

        QueueToken queueToken = QueueToken.create(command.productId(), command.userId());

        // redis 대기열 저장소 저장 & 중복 진입 차단
        boolean isSuccess = queueRepository.register(queueToken);
        if (!isSuccess) {
            // 이미 대기열에 있는 경우 예외 처리
            throw new IllegalStateException("이미 대기열에 등록된 사용자입니다.");
        }

        // 현재 순번 조회
        Long waitingRank = queueRepository.getWaitingRank(command.productId(), queueToken.getId());
        // 요청시간 LocalDateTime 타입으로 변환
        LocalDateTime enteredAt = convertLocalDateTime(queueToken.getRequestTime());

        return QueueRedisResponse.builder()
            .token(queueToken.getId().getValue())
            .productId(queueToken.getProductId())
            .rank(waitingRank)
            .status(queueToken.getStatus())
            .enteredAt(enteredAt)
            .build();
    }

    /**
     * 대기열 순번, 상태 조회 (polling)
     */
    @Override
    public QueueRedisResponse getQueueRank(UUID productId, String token, Long userId, String role) {
        TokenId tokenId = validateQueueToken(token);

        // 활성 상태 여부 확인
        if (queueRepository.isActivatedToken(productId, tokenId)) {
            return QueueRedisResponse.builder()
                .token(tokenId.getValue())
                .productId(productId)
                .rank(0L) // 활성 상태는 순번 0 (이미 활성열에 있으므로)
                .status(QueueStatus.ACTIVE)
                .enteredAt(LocalDateTime.now()) // 활성 상태일 때, 진입시간 현재시간으로 설정
                .build();
        }

        // 대기열 순번 확인 (Redis ZRANK)
        // rank는 0부터 시작 (내 앞의 대기 인원 수 (0이면 내가 1빠))
        Long waitingRank = queueRepository.getWaitingRank(productId, tokenId);
        if (waitingRank == null) {
            // Redis에 없으면 만료되었거나 잘못된 토큰
            throw new IllegalArgumentException("대기열에 존재하지 않는 토큰입니다.");
        }

        // 요청시간 LocalDateTime 타입으로 변환
        Long requestTime = getRequestTime(productId, tokenId);
        LocalDateTime enteredAt = convertLocalDateTime(requestTime);

        return QueueRedisResponse.builder()
            .token(tokenId.getValue())
            .productId(productId)
            .rank(waitingRank + 1) // 사용자 친화적 순번 (0번대신 1번부터 표시)
            .status(QueueStatus.WAITING)
            .enteredAt(enteredAt)
            .build();
    }

    /**
     * 타임스탬프 -> LocalDateTime 변환
     */
    private LocalDateTime convertLocalDateTime(Long timestamp) {
        // 시스템 기본 타임존 사용 (Asia/Seoul)
        if (timestamp == null) { return null; }
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }

    private Long getRequestTime(UUID productId, TokenId tokenId) {
        // 진입 요청 시간 반환
        Double score = queueRepository.getWaitingScore(productId, tokenId);
        return score != null ? score.longValue() : System.currentTimeMillis();
    }

    private TokenId validateQueueToken(String token) {
        TokenId tokenId;
        try {
            tokenId = TokenId.of(UUID.fromString(token));
        } catch (IllegalArgumentException e) {
            // TODO : BUSINESSEXCEPTION으로 수정 필요
            throw new IllegalArgumentException("잘못된 토큰 형식입니다.");
        }
        return tokenId;
    }
}
