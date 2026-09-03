package com.rushcrew.payment_service.application.service;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.payment_service.application.command.PaymentCommand;
import com.rushcrew.payment_service.application.result.PaymentPrepareResult;
import com.rushcrew.payment_service.application.result.PaymentResult;
import com.rushcrew.payment_service.domain.exception.PaymentErrorCode;
import com.rushcrew.payment_service.domain.model.Payment;
import com.rushcrew.payment_service.domain.repository.PaymentRepository;
import com.rushcrew.payment_service.infrastructure.adpater.PortonePaymentAdapter;
import com.rushcrew.payment_service.infrastructure.client.OrderClient;
import com.rushcrew.payment_service.infrastructure.client.dto.OrderResponse;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.webhook.Webhook;
import io.portone.sdk.server.webhook.WebhookTransaction;
import io.portone.sdk.server.webhook.WebhookVerifier;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WebhookVerifier portoneWebhook;
    private final OrderClient orderClient;
    private final PortonePaymentAdapter adapter;
    private final PaymentTransactionExecutor transactionExecutor;

    @Transactional
    public PaymentPrepareResult preparePayment(PaymentCommand command) {
        try {
            OrderResponse orderResponse = orderClient.getOrder(command.orderId());

            if (!orderResponse.totalAmount().equals(command.totalAmount())) {
                throw new BusinessException(PaymentErrorCode.AMOUNT_MISMATCH);
            }
        } catch (FeignException.NotFound e) {
            throw new BusinessException(PaymentErrorCode.ORDER_NOT_FOUND);
        } catch (FeignException e) {
            throw new BusinessException(PaymentErrorCode.ORDER_NOT_FOUND);
        }

        String portOnePaymentId = UUID.randomUUID().toString();

        Payment payment = Payment.create(
                command.orderId(),
                command.totalAmount(),
                portOnePaymentId
        );

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentPrepareResult.of(portOnePaymentId, savedPayment);
    }

    public PaymentResult completePayment(String portOnePaymentId) {
        Payment payment = paymentRepository.findByPortonePaymentId(portOnePaymentId)
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.INVALID_PAYMENT));

        PaidPayment actualPayment = adapter.getPayment(portOnePaymentId); // PG 호출은 트랜잭션 밖에서 수행

        return transactionExecutor.processPayment(payment, actualPayment);
    }

    public PaymentResult cancelPayment(UUID paymentId, String cancelReason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.INVALID_PAYMENT));

        try {
            adapter.cancelPayment(payment.getPortonePaymentId(), cancelReason); // PG 호출은 트랜잭션 밖에서 수행
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(PaymentErrorCode.FAILED_CANCEL_PAYMENT);
        }

        return transactionExecutor.applyCancellation(payment);
    }

    public PaymentResult findPaymentByPaymentId(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.INVALID_PAYMENT));

        return PaymentResult.from(payment);
    }

    public PaymentResult findPaymentByOrderId(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.INVALID_PAYMENT));

        return PaymentResult.from(payment);
    }

    public void handleWebhook(String body, String webhookId, String webhookTimestamp, String webhookSignature) throws Exception {
        Webhook webhook;
        try {
            webhook = portoneWebhook.verify(body, webhookId, webhookSignature, webhookTimestamp);
        } catch (Exception e) {
            throw new BusinessException(PaymentErrorCode.INVALID_WEBHOOK);
        }
        if (webhook instanceof WebhookTransaction transaction) {
            completePayment(transaction.getData().getPaymentId());
        }
    }
}