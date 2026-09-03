package com.rushcrew.payment_service.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.payment_service.application.command.PaymentCommand;
import com.rushcrew.payment_service.application.service.PaymentService;
import com.rushcrew.payment_service.domain.exception.PaymentErrorCode;
import com.rushcrew.payment_service.domain.model.Payment;
import com.rushcrew.payment_service.domain.repository.PaymentRepository;
import com.rushcrew.payment_service.infrastructure.event.PaymentMessageStatus;
import com.rushcrew.payment_service.infrastructure.event.PaymentRequestMessage;
import com.rushcrew.payment_service.infrastructure.event.PaymentResultMessage;
import io.portone.sdk.server.errors.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTransactionConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @KafkaListener(topics = "payment-transaction-result", groupId = "payment-transaction-result-group")
    public void consumePaymentTransactionResultEvent(final String paymentResponseMessage) {
        try {
            final PaymentResultMessage paymentResultMessage =
                    objectMapper.readValue(paymentResponseMessage, PaymentResultMessage.class);

            if (paymentResultMessage.messageStatus() == PaymentMessageStatus.FAILED) {
                Payment payment = paymentRepository.findById(paymentResultMessage.paymentId())
                        .orElseThrow(() -> new BusinessException(PaymentErrorCode.INVALID_PAYMENT));

                switch (payment.getStatus()) {
                    case PENDING -> {
                        payment.failPayment();
                        try {
                            paymentRepository.save(payment);
                        } catch (ObjectOptimisticLockingFailureException e) {
                            log.warn("동시 업데이트 감지 - 보상 트랜잭션 재시도 필요: {}", payment.getPaymentId());
                            throw new RuntimeException("Optimistic lock failed - will retry", e);
                        }
                    }
                    case PAID -> {
                        paymentService.cancelPayment(payment.getPaymentId(), "Saga rollback");
                    }
                    case CANCELLED, FAILED -> {
                        log.info("결제가 이미 취소되었거나 실패했습니다 {}", payment.getPaymentId());
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Deserialization 실패", e);
        } catch (Exception e) {
            throw new RuntimeException("Processing 실패", e);
        }
    }

    @KafkaListener(topics = "payment-request", groupId = "payment-request-result-group")
    public void consumePaymentRequestEvent(final String requestMessage) {
        try {
            PaymentRequestMessage paymentRequestMessage = objectMapper.readValue(requestMessage, PaymentRequestMessage.class);

            PaymentCommand paymentCommand = new PaymentCommand(paymentRequestMessage.orderId(),
                    paymentRequestMessage.finalAmount());
            paymentService.preparePayment(paymentCommand);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Deserialization 실패", e);
        } catch (Exception e) {
            throw new RuntimeException("Processing 실패", e);
        }
    }
}
