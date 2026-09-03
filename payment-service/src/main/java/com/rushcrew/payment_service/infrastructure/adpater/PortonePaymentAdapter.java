package com.rushcrew.payment_service.infrastructure.adpater;

import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.PaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortonePaymentAdapter {

    private final PaymentClient client;

    public PaidPayment getPayment(String portonePaymentId) {
        var payment = client.getPayment(portonePaymentId).join();
        if (payment instanceof PaidPayment paidPayment) {
            return paidPayment;
        }
        throw new RuntimeException("결제가 실패했습니다.");
    }

    public void cancelPayment(String portonePaymentId, String reason) {
        client.cancelPayment(
                portonePaymentId,
                null,
                null,
                null,
                reason,
                null,
                null,
                null
        ).join();
    }
}
