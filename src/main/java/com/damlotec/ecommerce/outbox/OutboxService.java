package com.damlotec.ecommerce.outbox;

import com.damlotec.ecommerce.kafka.OrderConfirmation;
import com.damlotec.ecommerce.kafka.OrderProducer;
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
    private final OrderProducer orderProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 120000)
    public void pollOutboxMessagesAndPublish() {
        log.info("Polling outbox messages");
        List<Outbox> unprocessedRecord = outboxRepository.findByStatusFalse();
        log.info("Unprocessed record count: {}", unprocessedRecord.size());
        unprocessedRecord.forEach(outbox -> {
            try {
                OrderConfirmation orderConfirmation = objectMapper.readValue(outbox.getPayload(), OrderConfirmation.class);
                orderProducer.sendOrderConfirmation(orderConfirmation);
                outbox.setStatus(Boolean.TRUE);
                outboxRepository.save(outbox);
            } catch (Exception e) {
                log.error("Error sending order confirmation {}", e.getMessage());
            }
        });
    }
}
