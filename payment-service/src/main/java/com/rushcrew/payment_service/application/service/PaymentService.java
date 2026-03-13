package com.rushcrew.payment_service.application.service;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.payment_service.application.command.PaymentCommand;
import com.rushcrew.payment_service.application.mapper.PaymentMapper;
import com.rushcrew.payment_service.application.result.PaymentPrepareResult;
import com.rushcrew.payment_service.application.result.PaymentResult;
import com.rushcrew.payment_service.domain.exception.PaymentErrorCode;
import com.rushcrew.payment_service.domain.model.Payment;
import com.rushcrew.payment_service.domain.model.PaymentTransaction;
import com.rushcrew.payment_service.domain.repository.PaymentRepository;
import com.rushcrew.payment_service.domain.repository.PaymentTransactionRepository;
import com.rushcrew.payment_service.domain.vo.Amount;
import com.rushcrew.payment_service.domain.vo.Card;
import com.rushcrew.payment_service.infrastructure.adpater.PortonePaymentAdapter;
import com.rushcrew.payment_service.infrastructure.client.OrderClient;
import com.rushcrew.payment_service.infrastructure.client.dto.OrderResponse;
import com.rushcrew.payment_service.infrastructure.event.PaymentEventProducer;
import feign.FeignException;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.PaymentMethodCard;
import io.portone.sdk.server.webhook.Webhook;
import io.portone.sdk.server.webhook.WebhookTransaction;
import io.portone.sdk.server.webhook.WebhookVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final WebhookVerifier portoneWebhook;
    private final PaymentEventProducer paymentEventProducer;
    private final OrderClient orderClient;
    private final PortonePaymentAdapter adapter;

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

    @Transactional
    public Mono<PaymentResult> completePayment(String portOnePaymentId) {
        Payment payment = paymentRepository.findByPortonePaymentId(portOnePaymentId)
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.INVALID_PAYMENT));

        return adapter.getPayment(portOnePaymentId)
                .flatMap(actualPayment -> processPayment(payment, actualPayment));
    }

    @Transactional
    public Mono<PaymentResult> cancelPayment(UUID paymentId, String cancelReason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.INVALID_PAYMENT));

        String portonePaymentId = payment.getPortonePaymentId();

        return adapter.cancelPayment(portonePaymentId, cancelReason)
                .flatMap(cancelResponse -> {
                    payment.cancelPayment();
                    paymentRepository.save(payment);

                    return Mono.just(PaymentResult.from(payment));
                })
                .onErrorMap(e -> new BusinessException(PaymentErrorCode.FAILED_CANCEL_PAYMENT));
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

    public Mono<Void> handleWebhook(String body, String webhookId, String webhookTimestamp, String webhookSignature) throws Exception {
        Webhook webhook;
        try {
            webhook = portoneWebhook.verify(body, webhookId, webhookSignature, webhookTimestamp);
        } catch (Exception e) {
            throw new BusinessException(PaymentErrorCode.INVALID_WEBHOOK);
        }
        if (webhook instanceof WebhookTransaction transaction) {
            return completePayment(transaction.getData().getPaymentId())
                    .then();
        }
        return Mono.empty();
    }

    private Mono<PaymentResult> processPayment(Payment payment, Object actualPayment) {

        if (actualPayment instanceof PaidPayment paidPayment) {

            try {
                payment.verifyPaymentOrThrow(
                        paidPayment.getAmount().getPaid(),
                        paidPayment.getCurrency().getValue()
                );
            } catch (IllegalArgumentException e) {
                return Mono.error(new BusinessException(PaymentErrorCode.FAILED_VERIFYING_PAYMENT));
            }

            payment.completePayment();
            paymentRepository.save(payment);

            if (paidPayment.getMethod() instanceof PaymentMethodCard paymentMethodCard) {
                PaymentTransaction transaction = PaymentMapper.toPaymentTransaction(paidPayment, payment);

                Card card = PaymentMapper.toCard(paymentMethodCard);
                transaction.addCard(card);

                Amount amount = PaymentMapper.toAmount(paidPayment);
                transaction.addAmount(amount);

                paymentTransactionRepository.save(transaction);
            }

            paymentEventProducer.completePayment(
                    payment.getPaymentId(),
                    payment.getOrderId(),
                    payment.getAmount(),
                    paidPayment.getCurrency().getValue()
            );

            return Mono.just(PaymentResult.from(payment));
        }
        return Mono.error(new BusinessException(PaymentErrorCode.NOT_COMPLETED_PAYMENT));
    }
}
