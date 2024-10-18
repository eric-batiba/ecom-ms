package com.damlotec.ecommerce.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static com.damlotec.ecommerce.config.PaymentTopicConfig.PAYMENT_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    public final KafkaTemplate<String, PaymentNotification> kafkaTemplate;

    public void sendPaymentNotification(PaymentNotification paymentNotification) {
        log.info("Sending payment notification to kafka :: {}", paymentNotification);
        Message<PaymentNotification> message = MessageBuilder
                .withPayload(paymentNotification)
                .setHeader(KafkaHeaders.TOPIC, PAYMENT_TOPIC)
                .build();
        CompletableFuture<SendResult<String, PaymentNotification>> completableFuture = kafkaTemplate.send(message);
        completableFuture.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Error sending payment notification to kafka :: {}", exception.getMessage());
            } else {
                log.info(String.format("Payment notification message : %s - sent to kafka successfully with offset : %d", message, result.getRecordMetadata().offset()));
            }
        });
    }
}
