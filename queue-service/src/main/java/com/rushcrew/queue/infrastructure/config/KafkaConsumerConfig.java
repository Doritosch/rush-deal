package com.rushcrew.queue.infrastructure.config;

import com.rushcrew.queue.application.port.in.TokenRemoveEvent;
import com.rushcrew.queue.infrastructure.kafka.consumer.QueueEventConsumer;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    /**
     * KafkaTemplate 설정
     * Json 직렬화 가능한 ProducerFactory 사용
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Producer Factory 설정
     * DLQ로 메시지를 보낼 때(Produce), 자바 객체를 JSON으로 직렬화하기 위해 필요함
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // value 보낼 때 JsonSerializer 사용하도록 설정
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * 컨슈머 팩토리 설정
     * JSON 메시지 어떻게 역직렬화할지 정의
     */
    @Bean
    public ConsumerFactory<String, TokenRemoveEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        // 로컬 환경: localhost:9092 / Docker 내부 통신: kafka:29092
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "queue-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // JSON 역직렬화 설정
        JsonDeserializer<TokenRemoveEvent> deserializer = new JsonDeserializer<>(TokenRemoveEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*"); // 모든 패키지 신뢰
        deserializer.setUseTypeMapperForKey(true);

        // ErrorHandlingDeserializer: 역직렬화 실패 시 무한 루프 방지
        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            new ErrorHandlingDeserializer<>(deserializer)
        );
    }

    /**
     * 리스너 컨테이너 팩토리 (에러 핸들러 적용)
     * 여기서 재시도(Retry) 및 DLQ(Dead Letter Queue) 전략 주입
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TokenRemoveEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TokenRemoveEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // 수동 커밋 사용 시 설정 (Listener에서 Acknowledgment 사용 시 필요)
        factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);

        // 에러 핸들러 설정 (재시도 + DLQ)
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    /**
     * Kafka 에러 핸들러 처리
     * 1. 재시도(Retry): 1초 간격으로 최대 3회 재시도 (FixedBackOff)
     * 2. 복구 전략 - DLQ 이동: 3회 실패 시 '토픽명.DLT'로 메시지 이동 (Dead Letter Topic)
     */
    @Bean
    public CommonErrorHandler errorHandler() {
        // DLT 발행자: 실패한 메시지를 '원본토픽명.DLT'로 자동 발행
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate(),
            (record, ex) -> {
                log.error("[QUEUE:Kafka:DLQ] 3회 재시도 실패, DLQ로 이동합니다. TOPIC: {}, Value: {}",
                    record.topic(), record.value());
                return new TopicPartition(record.topic() + ".DLT", record.partition());
            });

        // 백오프(재시도) 전략: 1000ms(1초) 간격으로 대기 후 재시도, 최대 3번
        // (총 3번 실행 후 실패하면 Recoverer 실행)
        FixedBackOff backOff = new FixedBackOff(1000L, 3L);
        // 에러 핸들러 생성
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // 재시도할 필요 없는 치명적 예외는 바로 DLT로 보냄 (예: JSON 파싱 에러)
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }
}