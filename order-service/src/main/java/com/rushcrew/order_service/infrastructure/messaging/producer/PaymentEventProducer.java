package com.rushcrew.order_service.infrastructure.messaging.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.order_service.infrastructure.dto.payment.PaymentRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private static final String PAYMENT_REQUEST_TOPIC = "payment-request";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendPaymentRequest(PaymentRequestMessage requestMessage) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(requestMessage);
        kafkaTemplate.send(PAYMENT_REQUEST_TOPIC, message);
    }
}
