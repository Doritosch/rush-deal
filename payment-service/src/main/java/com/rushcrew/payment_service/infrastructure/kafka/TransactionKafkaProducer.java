package com.rushcrew.payment_service.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.payment_service.infrastructure.event.PaymentCompletedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionKafkaProducer {

    private static final String PAYMENT_COMPLETED_TOPIC = "payment-complete-result";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void completePayment(final PaymentCompletedMessage paymentCompletedMessage) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(paymentCompletedMessage);
        kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, message);
    }
}
