package com.damlotec.ecommerce.outbox;

import com.damlotec.ecommerce.kafka.OrderProducer;
import com.damlotec.ecommerce.payment.PaymentClient;
import com.damlotec.ecommerce.payment.PaymentMapper;
import com.damlotec.ecommerce.payment.PaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
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
    private final PaymentMapper paymentMapper;
    private final PaymentClient paymentClient;

    @Scheduled(fixedRate = 150000)
    public void pollOutboxMessagesAndPublish() {
        log.info("Polling outbox messages");
        List<Outbox> unprocessedRecords = outboxRepository.findByStatusFalse();
        if (unprocessedRecords.isEmpty()) {
            log.info("zero message to process.");
            return;
        }
        log.info("Unprocessed record count: {}", unprocessedRecords.size());
        unprocessedRecords.parallelStream().forEach(outbox -> {
            try {
                com.avro.OrderConfirmation orderConfirmation = objectMapper.readValue(outbox.getPayload(), com.avro.OrderConfirmation.class);
                log.info("OrderConfirmation: {}", orderConfirmation);

                processPayment(orderConfirmation);

                orderProducer.sendOrderConfirmation(orderConfirmation);
                outbox.setStatus(Boolean.TRUE);
                outboxRepository.save(outbox);
                log.info("Outbox sent successfully: {}", outbox);

                log.info(" Successful processing outbox message for orderId : {}", orderConfirmation.getOrderId());

            } catch (FeignException fe) {
                log.error("Payment failed for orderId {}: {}", outbox.getId(), fe.getMessage());
                // Optional : Add a logic of DLQ here
            } catch (Exception e) {
                log.error("Error sending order confirmation for ID: {} : {}", outbox.getId(), e.getMessage());
            }
        });
    }

    private void processPayment(com.avro.OrderConfirmation orderConfirmation) {
        try {
            log.info("OrderConfirmation in processPayment: {}", orderConfirmation);
            PaymentRequest request = paymentMapper.toPaymentRequest(orderConfirmation);
            log.info("Payment request in processPayment: {}", request);

            // Call Feign Payment Service
            paymentClient.pay(request);

            log.info("Payment successfully process for orderId : {}", orderConfirmation.getOrderId());
        } catch (FeignException fe) {
            log.error(" Error occur during call payment service : {}", fe.getMessage());
            throw fe; // Send back exception for next time call
        }
    }
}
