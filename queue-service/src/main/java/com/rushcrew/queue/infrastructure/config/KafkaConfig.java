package com.rushcrew.queue.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String ORDER_COMPLETE_TOPIC = "order-complete-token-remove";

    /**
     * 애플리케이션 로딩 시점에 'order-complete-token-remove' 토픽이 없으면 자동 생성
     * partitions(1): 순서 보장이 중요하다면 1, 병렬 처리가 중요하면 늘림 (MVP는 1 추천)
     * replicas(1): 로컬 단일 브로커이므로 1
     */
    @Bean
    public NewTopic orderCompleteTopic() {
        return TopicBuilder.name(ORDER_COMPLETE_TOPIC)
            .partitions(1)
            .replicas(1)
            .build();
    }
}
