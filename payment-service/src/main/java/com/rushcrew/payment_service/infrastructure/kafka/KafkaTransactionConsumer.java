package com.rushcrew.payment_service.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.payment_service.infrastructure.event.PaymentMessageStatus;
import com.rushcrew.payment_service.infrastructure.event.PaymentRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTransactionConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-transaction-result", groupId = "payment-transaction-result-group")
    public void consumePaymentTransactionResultEvent(final String paymentResponseMessage) throws JsonProcessingException {
        final PaymentRequestMessage paymentRequestMessage = objectMapper.readValue(paymentResponseMessage, PaymentRequestMessage.class);

        if (paymentRequestMessage.messageStatus() == PaymentMessageStatus.FAILED) {

        }
    }
}
