package com.rushcrew.payment_service.application.mapper;

import com.rushcrew.payment_service.domain.model.Payment;
import com.rushcrew.payment_service.domain.model.PaymentTransaction;
import com.rushcrew.payment_service.domain.vo.Amount;
import com.rushcrew.payment_service.domain.vo.Card;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.PaymentMethodCard;

public class PaymentMapper {
    public static Card toCard(PaymentMethodCard paymentMethodCard) {
        return new Card(
                paymentMethodCard.getCard().getPublisher(),
                paymentMethodCard.getCard().getIssuer(),
                paymentMethodCard.getCard().getBrand().toString(),
                paymentMethodCard.getCard().getType().toString(),
                paymentMethodCard.getCard().getOwnerType().toString(),
                paymentMethodCard.getCard().getBin(),
                paymentMethodCard.getCard().getName(),
                paymentMethodCard.getCard().getNumber()
        );
    }

    public static Amount toAmount(PaidPayment paidPayment) {
        return new Amount(
                paidPayment.getAmount().getTotal(),
                paidPayment.getAmount().getTaxFree(),
                paidPayment.getAmount().getVat(),
                paidPayment.getAmount().getSupply(),
                paidPayment.getAmount().getDiscount(),
                paidPayment.getAmount().getPaid()
        );
    }

    public static PaymentTransaction toPaymentTransaction(PaidPayment paidPayment, Payment payment) {
        return PaymentTransaction.create(
                payment,
                paidPayment.getId(),
                paidPayment.getTransactionId(),
                paidPayment.getStoreId(),
                paidPayment.getRequestedAt(),
                paidPayment.getUpdatedAt(),
                paidPayment.getStatusChangedAt()
        );
    }
}
