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
        if (!status.isPending()) {
            throw new IllegalArgumentException("결제 요청 상태에서만 완료할 수 있습니다.");
        }
        this.status = PaymentStatus.PAID;
    }

    public void cancelPayment() {
        if (!status.isPaid()) {
            throw new IllegalArgumentException("결제 완료 상태에서만 취소할 수 있습니다.");
        }
        this.status = PaymentStatus.CANCELLED;
    }
}