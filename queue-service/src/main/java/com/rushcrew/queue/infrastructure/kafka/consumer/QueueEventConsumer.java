package com.rushcrew.queue.infrastructure.kafka.consumer;

import com.rushcrew.queue.application.port.in.QueuePort;
import com.rushcrew.queue.application.port.in.TokenRemoveEvent;
import com.rushcrew.queue.application.service.QueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QueueEventConsumer {

    private final QueuePort queueService;

    public QueueEventConsumer(QueueService queueService) {
        this.queueService = queueService;
    }

    /**
     * 주문 완료 시 발행되는 토큰 만료 요청 이벤트를 수신
     * Topic: order-complete-token-remove
     */
    @KafkaListener(topics = "order-complete-token-remove", groupId = "queue-service-group")
    public void consumeTokenRemoveEvent(TokenRemoveEvent event, Acknowledgment ack) {
        log.info("[QUEUE:Kafka:Consume] 토큰 삭제 요청 수신 - UserId: {}, Token: {}", event.userId(), event.token());

        // 토큰 삭제 & 유저 인덱스 삭제
        // 여기서 예외 발생 시, try-catch가 없으므로 즉시 에러 핸들러로 넘어감 -> 재시도 시작
        queueService.exitQueue(
            event.productId(),
            event.token(),
            event.userId()
        );
        log.info("[QUEUE:Kafka:Success] 토큰 삭제 완료 - UserId: {}, ProductId: {}",
            event.userId(), event.productId());

        // 수동 커밋 실행 (성공 시에만)
        // KafkaConsumerConfig에 AckMode.MANUAL_IMMEDIATE가 설정되어 있으므로 필수
        ack.acknowledge();

        log.info("[QUEUE:Kafka:Success] 토큰 삭제 완료 및 Offset 커밋 - UserId: {}", event.userId());
    }
}
