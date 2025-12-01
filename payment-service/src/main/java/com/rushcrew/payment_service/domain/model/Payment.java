package com.rushcrew.payment_service.domain.model;

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
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}