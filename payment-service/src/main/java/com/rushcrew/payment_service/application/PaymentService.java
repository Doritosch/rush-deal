package com.rushcrew.payment_service.application;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.payment_service.application.command.PaymentCommand;
import com.rushcrew.payment_service.application.result.PaymentPrepareResult;
import com.rushcrew.payment_service.application.result.PaymentResult;
import com.rushcrew.payment_service.domain.exception.PaymentErrorCode;
import com.rushcrew.payment_service.domain.model.Payment;
import com.rushcrew.payment_service.domain.model.PaymentTransaction;
import com.rushcrew.payment_service.domain.repository.PaymentRepository;
import com.rushcrew.payment_service.domain.repository.PaymentTransactionRepository;
import com.rushcrew.payment_service.domain.vo.Amount;
import com.rushcrew.payment_service.domain.vo.Card;
import com.rushcrew.payment_service.presentation.dto.response.PaymentPrepareResponse;
import com.rushcrew.payment_service.presentation.dto.response.PaymentResponse;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.PaymentClient;
import io.portone.sdk.server.payment.PaymentMethodCard;
import io.portone.sdk.server.webhook.Webhook;
import io.portone.sdk.server.webhook.WebhookTransaction;
import io.portone.sdk.server.webhook.WebhookVerifier;
import kotlin.Unit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    private final PaymentClient portone;
    private final WebhookVerifier portoneWebhook;

    @Transactional
    public PaymentPrepareResult preparePayment(PaymentCommand command) {
        // TODO: orderId로 상품 정보 조회하여 amount 검증

        Payment payment = Payment.create(
                command.orderId(),
                command.totalAmount()
        );

        Payment savedPayment = paymentRepository.save(payment);

        String portOnePaymentId = UUID.randomUUID().toString();

        return PaymentPrepareResult.of(portOnePaymentId, savedPayment);
    }

    @Transactional
    public Mono<PaymentResponse> completePayment(UUID paymentId, String portOnePaymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.INVALID_PAYMENT));

        return Mono.fromFuture(portone.getPayment(portOnePaymentId))
                .flatMap(actualPayment -> {
                    switch (actualPayment) {
                        case PaidPayment paidPayment:
                            if (!verifyPayment(payment, paidPayment)) {
                                return Mono.error(new BusinessException(PaymentErrorCode.FAILED_VERIFYING_PAYMENT));
                            }

                            payment.completePayment();
                            paymentRepository.save(payment);

                            if (paidPayment.getMethod() instanceof PaymentMethodCard paymentMethodCard) {
                                PaymentTransaction transaction = PaymentTransaction.create(
                                        payment,
                                        paidPayment.getId(),
                                        paidPayment.getTransactionId(),
                                        paidPayment.getStoreId(),
                                        paidPayment.getCurrency().getValue(),
                                        paidPayment.getRequestedAt(),
                                        paidPayment.getUpdatedAt(),
                                        paidPayment.getStatusChangedAt()
                                );

                                Card card = new Card(
                                        paymentMethodCard.getCard().getPublisher(),
                                        paymentMethodCard.getCard().getIssuer(),
                                        paymentMethodCard.getCard().getBrand().toString(),
                                        paymentMethodCard.getCard().getType().toString(),
                                        paymentMethodCard.getCard().getOwnerType().toString(),
                                        paymentMethodCard.getCard().getBin(),
                                        paymentMethodCard.getCard().getName(),
                                        paymentMethodCard.getCard().getNumber()
                                );
                                transaction.addCard(card);

                                Amount amount = new Amount(
                                        paidPayment.getAmount().getTotal(),
                                        paidPayment.getAmount().getTaxFree(),
                                        paidPayment.getAmount().getVat(),
                                        paidPayment.getAmount().getSupply(),
                                        paidPayment.getAmount().getDiscount(),
                                        paidPayment.getAmount().getPaid()
                                );
                                transaction.addAmount(amount);

                                paymentTransactionRepository.save(transaction);
                            }
                            return Mono.just(PaymentResponse.from(PaymentResult.from(payment)));
                        default:
                            return Mono.error(new BusinessException(PaymentErrorCode.NOT_COMPLETED_PAYMENT));
                    }
                });
    }

    public Mono<Unit> handleWebhook(String body, String webhookId, String webhookTimestamp, String webhookSignature) throws Exception {
        Webhook webhook;
        try {
            webhook = portoneWebhook.verify(body, webhookId, webhookSignature, webhookTimestamp);
        } catch (Exception e) {
            throw new Exception();
        }
        if (webhook instanceof WebhookTransaction transaction) {
            return syncPayment(transaction.getData().getPaymentId()).map(payment -> Unit.INSTANCE);
        }
        return Mono.empty();
    }

    @Transactional
    public Mono<Unit> syncPayment(String portOnePaymentId) {
        return Mono.fromFuture(portone.getPayment(portOnePaymentId))
                .flatMap(actualPayment -> {
                    switch (actualPayment) {
                        case PaidPayment paidPayment:
                            return Mono.error(new BusinessException(PaymentErrorCode.NOT_FOUND_PORTONE_ID));
                        default:
                            return Mono.just(Unit.INSTANCE);
                    }
                });
    }

    private boolean verifyPayment(Payment payment, PaidPayment paidPayment) {
        BigDecimal expectedAmount = payment.getAmount();
        long actualAmount = paidPayment.getAmount().getTotal();

        if (expectedAmount.longValue() != actualAmount) {
            return false;
        }
        if (!"KRW".equals(paidPayment.getCurrency().getValue())) {
            return false;
        }
        return true;
    }
}
