package com.rushcrew.payment_service.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.payment_service.application.command.PaymentCommand;
import com.rushcrew.payment_service.application.service.PaymentService;
import com.rushcrew.payment_service.infrastructure.event.PaymentMessageStatus;
import com.rushcrew.payment_service.infrastructure.event.PaymentRequestMessage;
import com.rushcrew.payment_service.infrastructure.event.PaymentResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTransactionConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    @KafkaListener(topics = "payment-transaction-result", groupId = "payment-transaction-result-group")
    public void consumePaymentTransactionResultEvent(final String paymentResponseMessage) throws JsonProcessingException {
        final PaymentResultMessage paymentResultMessage = objectMapper.readValue(paymentResponseMessage, PaymentResultMessage.class);

        if (paymentResultMessage.messageStatus() == PaymentMessageStatus.FAILED) {
            paymentService.cancelPayment(paymentResultMessage.paymentId(), "Payment Messaging 실패");
        }
    }

    @KafkaListener(topics = "payment-request", groupId = "payment-request-result-group")
    public void consumePaymentRequestEvent(final String requestMessage) throws JsonProcessingException {
        PaymentRequestMessage paymentRequestMessage = objectMapper.readValue(requestMessage, PaymentRequestMessage.class);

        PaymentCommand paymentCommand = new PaymentCommand(paymentRequestMessage.orderId(),
                paymentRequestMessage.finalAmount());
        paymentService.preparePayment(paymentCommand);
    }
}
