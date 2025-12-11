package com.rushcrew.queue.infrastructure.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.queue.application.port.in.TokenRemoveEvent;
import com.rushcrew.queue.application.service.QueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QueueEventConsumer {

    private final QueueService queueService;
    private final ObjectMapper objectMapper;

    public QueueEventConsumer(QueueService queueService, ObjectMapper objectMapper) {
        this.queueService = queueService;
        this.objectMapper = objectMapper;
    }

    /**
     * 주문 완료 시 발행되는 토큰 만료 요청 이벤트를 수신
     * Topic: queue-token-remove-topic
     */
    @KafkaListener(topics = "queue-token-remove-topic", groupId = "queue-service-group")
    public void consumeTokenRemoveEvent(String message) {
        try {
            log.info("[QUEUE:Kafka:Consume] 토큰 삭제 요청 수신: {}", message);

            TokenRemoveEvent event = objectMapper.readValue(message, TokenRemoveEvent.class);

            // 토큰 삭제 & 유저 인덱스 삭제
            queueService.exitQueue(
                event.productId(),
                event.token(),
                event.userId()
            );
            log.info("[QUEUE:Kafka:Success] 토큰 삭제 완료 - UserId: {}, ProductId: {}",
                event.userId(), event.productId());
        } catch (Exception e) {
            log.error("[QUEUE:Kafka:Error] 토큰 만료 처리 중 오류 발생. Message: {}", message, e);
            // 필요 시 Dead Letter Queue(DLQ)로 보내거나 재시도 로직 추가
        }
    }
}
