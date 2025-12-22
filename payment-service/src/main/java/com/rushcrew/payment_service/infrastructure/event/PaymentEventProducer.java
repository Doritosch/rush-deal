package com.rushcrew.payment_service.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private static final String PAYMENT_COMPLETED_TOPIC = "payment.completed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Publishing payment completed event: paymentId={}, orderId={}",
                event.paymentId(), event.orderId());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, event.orderId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Payment completed event published successfully: paymentId={}, orderId={}, offset={}",
                        event.paymentId(),
                        event.orderId(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish payment completed event: paymentId={}, orderId={}",
                        event.paymentId(),
                        event.orderId(),
                        ex);
            }
        });
    }
}
