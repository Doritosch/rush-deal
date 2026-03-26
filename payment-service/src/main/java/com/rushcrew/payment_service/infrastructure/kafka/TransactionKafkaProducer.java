package com.rushcrew.payment_service.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.payment_service.domain.model.PaymentOutbox;
import com.rushcrew.payment_service.domain.vo.OutboxStatus;
import com.rushcrew.payment_service.infrastructure.event.PaymentCompletedMessage;
import com.rushcrew.payment_service.infrastructure.repository.PaymentOutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionKafkaProducer {

    private static final String PAYMENT_COMPLETED_TOPIC = "payment-complete-result";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentOutboxRepository paymentOutboxRepository;

    public void completePayment(final PaymentCompletedMessage paymentCompletedMessage) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(paymentCompletedMessage);
        kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, message);
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<PaymentOutbox> pendingEvents =
                paymentOutboxRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING, PageRequest.of(0, 100));

        for(PaymentOutbox event : pendingEvents) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getPayload())
                        .whenComplete((result, ex) -> {
                    if (ex == null) {
                        event.markAsPublished();
                        paymentOutboxRepository.save(event);
                    } else {
                        event.incrementRetry();
                        if (event.getRetryCount() >= 3) {
                            event.markAsFailed();
                        }
                        paymentOutboxRepository.save(event);
                    }
                });
            } catch (Exception e) {
                event.incrementRetry();
                event.markAsFailed();
                paymentOutboxRepository.save(event);
            }
        }

    }
}
