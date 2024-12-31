package com.damlotec.ecommerce.outbox;

import com.avro.PaymentNotification;
import com.damlotec.ecommerce.kafka.PaymentNotificationWrapper;
import com.damlotec.ecommerce.kafka.PaymentProducer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final PaymentProducer paymentProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 120000)
    public void pollOutboxMessagesAndPublish() {
        log.info("Polling outbox messages");
        List<Outbox> unprocessedRecord = outboxRepository.findByStatusFalse();
        if (unprocessedRecord.isEmpty()) {
            log.info("zero message to precess.");
            return;
        }

        log.info("Unprocessed record count: {}", unprocessedRecord.size());
        unprocessedRecord.parallelStream().forEach(outbox -> {
            try {
                log.info("Processing outbox message: {}", outbox);
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                PaymentNotification paymentNotification = objectMapper.readValue(outbox.getPayload(), PaymentNotificationWrapper.class);

                log.info("Payment notification: {}", paymentNotification);
                paymentProducer.sendPaymentNotification(paymentNotification);
                outbox.setStatus(Boolean.TRUE);
                outboxRepository.save(outbox);

                log.info("Successfully sent out outbox: {}", outbox);
            } catch (Exception e) {
                log.error("Error sending order confirmation {}", e.getMessage());
            }
        });
    }
}
