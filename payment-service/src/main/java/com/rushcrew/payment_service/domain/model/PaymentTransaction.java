package com.rushcrew.payment_service.domain.model;

import com.rushcrew.payment_service.domain.vo.PaymentMethod;
import com.rushcrew.payment_service.domain.vo.PaymentStatus;
import com.rushcrew.payment_service.domain.vo.PaymentType;
import com.rushcrew.payment_service.domain.vo.Cancel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_payment_transaction", schema = "payment_schema")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentTransaction {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID providerId;

    private String paymentKey;

    private PaymentType type;

    private String mid;

    private String currency;

    private PaymentMethod method;

    private PaymentStatus status;

    private BigDecimal totalAmount;

    @Embedded
    private Cancel cancel;

    private Instant requestedAt;
    private Instant approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paymentId", nullable = false)
    private Payment payment;
}