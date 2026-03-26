package com.rushcrew.payment_service.domain.model;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.payment_service.domain.exception.PaymentErrorCode;
import com.rushcrew.payment_service.domain.vo.OutboxStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_payment_outbox", schema = "payment_schema")
public class PaymentOutbox {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String topic;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private Integer retryCount = 0;

    public static PaymentOutbox create(
            String topic,
            String payload
    ) {
        PaymentOutbox paymentOutbox = new PaymentOutbox();
        paymentOutbox.topic = topic;
        paymentOutbox.payload = payload;
        paymentOutbox.status = OutboxStatus.PENDING;
        paymentOutbox.createdAt = LocalDateTime.now();
        paymentOutbox.publishedAt = null;
        return paymentOutbox;
    }

    public void markAsPublished() {
        if (this.status == OutboxStatus.PUBLISHED) {
            throw new BusinessException(PaymentErrorCode.ALREADY_PUBLISHED_STATUS);
        }
        this.status = OutboxStatus.PUBLISHED;
    }

    public void markAsFailed() {
        if (this.status == OutboxStatus.FAILED) {
            throw new BusinessException(PaymentErrorCode.ALREADY_FAILED_STATUS);
        }
        this.status = OutboxStatus.FAILED;
    }

    public void incrementRetry() {
        this.retryCount += 1;
    }
}
