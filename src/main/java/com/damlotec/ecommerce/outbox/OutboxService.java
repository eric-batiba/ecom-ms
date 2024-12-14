package com.damlotec.ecommerce.outbox;

import com.damlotec.ecommerce.kafka.OrderProducer;
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

    @Scheduled(fixedRate = 6000)
    public void pollOutboxMessagesAndPublish() {
        log.info("Polling outbox messages");
        List<Outbox> unprocessedRecord = outboxRepository.findByStatusFalse();
        log.info("Unprocessed record count: {}", unprocessedRecord.size());
        unprocessedRecord.forEach(outbox -> {
            try {
                orderProducer.sendOrderConfirmation(outbox.getPayload());
                outbox.setStatus(Boolean.TRUE);
                outboxRepository.save(outbox);
            } catch (Exception e) {
                log.error("Error sending order confirmation {}", e.getMessage());
            }
        });
    }
}
