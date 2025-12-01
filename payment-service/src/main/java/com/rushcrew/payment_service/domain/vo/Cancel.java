package com.rushcrew.payment_service.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cancel {

    private BigDecimal cancelAmount;

    private String cancelReason;

    private Instant cancelledAt;

    private PaymentStatus cancelStatus;

    public Cancel(
            BigDecimal cancelAmount,
            String cancelReason
    ) {
        this.cancelAmount = cancelAmount;
        this.cancelReason = cancelReason;
        this.cancelledAt = Instant.now();
        this.cancelStatus = PaymentStatus.READY;
    }
}
