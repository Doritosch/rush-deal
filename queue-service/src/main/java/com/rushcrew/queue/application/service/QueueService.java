package com.rushcrew.queue.application.service;

import com.rushcrew.queue.application.command.EnterQueueCommand;
import com.rushcrew.queue.application.dto.QueueRedisResponse;
import com.rushcrew.queue.application.port.in.QueuePort;
import com.rushcrew.queue.domain.entity.QueueToken;
import com.rushcrew.queue.domain.repository.QueueRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Service;

@Service
public class QueueService implements QueuePort {
    private final QueueRepository queueRepository;

    public QueueService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    /**
     * 대기열 진입
     */
    @Override
    public QueueRedisResponse enterQueue(EnterQueueCommand command) {
        QueueToken queueToken = QueueToken.create(command.productId(), command.userId());

        // redis 대기열 저장소 저장
        queueRepository.register(queueToken);

        // 현재 순번 조회
        Long waitingRank = queueRepository.getWaitingRank(command.productId(), queueToken);
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
     * 타임스탬프 -> LocalDateTime 변환
     */
    private LocalDateTime convertLocalDateTime(Long timestamp) {
        // 시스템 기본 타임존 사용 (Asia/Seoul)
        if (timestamp == null) { return null; }
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }
}
