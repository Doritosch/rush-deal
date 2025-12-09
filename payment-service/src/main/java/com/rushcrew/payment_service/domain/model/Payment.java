package com.rushcrew.payment_service.domain.model;

import com.rushcrew.common.entity.BaseEntity;
import com.rushcrew.payment_service.domain.vo.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_payment", schema = "payment_schema")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public static Payment create(UUID orderId, BigDecimal amount) {
        Payment payment = new Payment();

        payment.orderId = orderId;
        payment.amount = amount;
        payment.status = PaymentStatus.PENDING;

        return payment;
    }

    public void completePayment() {
        this.status = PaymentStatus.PAID;
    }

    public void verifyPaymentOrThrow(Long amount, String currency) {
        if (!verifyAmount(amount)) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }
        if (!verifyCurrency(currency)) {
            throw new IllegalArgumentException("지원하지 않는 통화입니다:" + currency);
        }
    }
    private boolean verifyAmount(Long amount) {
        if (this.amount.longValue() != amount) {
            return false;
        }
        return true;
    }
    public boolean verifyCurrency(String currency) {
        if (!"KRW".equals(currency)) {
            return false;
        }
        return true;
    }
}