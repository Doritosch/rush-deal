package com.rushcrew.payment_service.domain.model;

import com.rushcrew.payment_service.domain.vo.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_payment_transaction", schema = "payment_schema")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentTransaction {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_transaction_id", nullable = false)
    private UUID id;

    @Column(name = "portone_payment_id")
    private String portonePaymentId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "store_id")
    private String storeId;

    @Embedded
    private Card card;

    private Instant requestedAt;
    private Instant updatedAt;
    private Instant statusChangedAt;

    @Embedded
    private Amount amount;

    private String currency;

    @Embedded
    private Cancel cancel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paymentId", nullable = false)
    private Payment payment;

    public static PaymentTransaction create(Payment payment, String portonePaymentId, String transactionId,
                                            String storeId, String currency, Instant requestedAt,
                                            Instant updatedAt, Instant statusChangedAt) {
        PaymentTransaction transaction = new PaymentTransaction();

        transaction.portonePaymentId = portonePaymentId;
        transaction.transactionId = transactionId;
        transaction.storeId = storeId;
        transaction.currency = currency;
        transaction.requestedAt = requestedAt;
        transaction.updatedAt = updatedAt;
        transaction.statusChangedAt = statusChangedAt;
        transaction.payment = payment;

        return transaction;
    }

    public void addCard(Card card) {
        this.card = card;
    }
    public void addAmount(Amount amount) {
        this.amount = amount;
    }
}