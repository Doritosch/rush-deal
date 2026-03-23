package com.rushcrew.order_service.infrastructure.messaging.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.order_service.application.command.service.RequestPaymentService;
import com.rushcrew.order_service.domain.model.order.Order;
import com.rushcrew.order_service.infrastructure.dto.payment.PaymentCompletedMessage;
import com.rushcrew.order_service.infrastructure.dto.payment.PaymentMessageStatus;
import com.rushcrew.order_service.infrastructure.dto.payment.PaymentResultMessage;
import com.rushcrew.order_service.infrastructure.messaging.producer.OrderEventProducer;
import com.rushcrew.order_service.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final static String PAYMENT_COMPLETE_RESULT_GROUP_ID = "payment-complete-result-group";

    private final ObjectMapper objectMapper;
    private final RequestPaymentService requestPaymentService;
    private final OrderJpaRepository orderJpaRepository;
    private final OrderEventProducer orderEventProducer;

    @KafkaListener(topics = "payment-complete", groupId = PAYMENT_COMPLETE_RESULT_GROUP_ID)
    public void setPaymentCompleteResultGroupId(String completedMessage) throws JsonProcessingException {
        PaymentCompletedMessage paymentCompletedMessage = objectMapper.readValue(completedMessage, PaymentCompletedMessage.class);

        requestPaymentService.paymentComplete(paymentCompletedMessage.orderId());
    }

    @KafkaListener(topics = "payment-complete-result", groupId = "order-transaction-result-group")
    public void consumeOrderPaymentTransactionResultEvent(final String paymentResponseMessage) throws JsonProcessingException {
        final PaymentCompletedMessage paymentCompletedMessage = objectMapper.readValue(paymentResponseMessage, PaymentCompletedMessage.class);
        PaymentMessageStatus resultStatus = PaymentMessageStatus.REWARDED;

        try {
            Order order = orderJpaRepository.findById(paymentCompletedMessage.orderId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("주문ID에 해당하는 주문을 찾지 못했습니다."));
            order.completePayment();
            orderJpaRepository.save(order);
        } catch (Exception e) {
            resultStatus = PaymentMessageStatus.FAILED;
        }
        PaymentResultMessage paymentResultMessage = new PaymentResultMessage(
                paymentCompletedMessage.paymentId(),
                paymentCompletedMessage.orderId(),
                paymentCompletedMessage.totalAmount(),
                paymentCompletedMessage.currency(),
                paymentCompletedMessage.completedAt(),
                paymentCompletedMessage.status(),
                resultStatus
        );
        orderEventProducer.sendTransactionResultMessage(paymentResultMessage);
    }
}
