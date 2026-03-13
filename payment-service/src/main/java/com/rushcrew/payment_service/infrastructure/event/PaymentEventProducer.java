package com.rushcrew.payment_service.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private static final String PAYMENT_COMPLETED_TOPIC = "payment-completed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void completePayment(UUID paymentId, UUID orderId, Long amount, String value) {
        PaymentCompletedEvent paymentCompletedEvent = PaymentCompletedEvent.of(paymentId, orderId, amount, value);
        kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, paymentCompletedEvent);
    }
}
