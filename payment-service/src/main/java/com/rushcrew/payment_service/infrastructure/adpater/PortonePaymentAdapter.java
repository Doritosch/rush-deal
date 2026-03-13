package com.rushcrew.payment_service.infrastructure.adpater;

import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.PaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PortonePaymentAdapter {

    private final PaymentClient client;

    public Mono<PaidPayment> getPayment(String portonePaymentId) {
        return Mono.fromFuture(client.getPayment(portonePaymentId))
                .map(payment -> {
                    if (payment instanceof PaidPayment paidPayment) {
                        return paidPayment;
                    }
                    throw new RuntimeException("결제가 실패했습니다.");
                });
    }

    public Mono<Void> cancelPayment(String portonePaymentId, String reason) {
        return Mono.fromFuture(
                client.cancelPayment(
                        portonePaymentId,
                        null,
                        null,
                        null,
                        reason,
                        null,
                        null,
                        null
                )
        ).then();
    }
}
