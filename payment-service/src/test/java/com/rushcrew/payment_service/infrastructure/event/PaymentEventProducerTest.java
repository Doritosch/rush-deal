package com.rushcrew.payment_service.infrastructure.event;

import com.rushcrew.payment_service.infrastructure.kafka.TransactionKafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"payment.completed"})
class PaymentEventProducerTest {

    @Autowired
    private TransactionKafkaProducer transactionKafkaProducer;

}
