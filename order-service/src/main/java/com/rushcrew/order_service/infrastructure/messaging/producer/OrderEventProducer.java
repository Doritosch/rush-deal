package com.rushcrew.order_service.infrastructure.messaging.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.order_service.infrastructure.dto.payment.PaymentRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final String PAYMENT_TRANSACTION_RESULT_TOPIC = "payment-transaction-result";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendTransactionResultMessage(final PaymentRequestMessage paymentRequestMessage) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(paymentRequestMessage);
        kafkaTemplate.send(PAYMENT_TRANSACTION_RESULT_TOPIC, message);
    }
}
