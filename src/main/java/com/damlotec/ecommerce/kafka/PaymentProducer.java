package com.damlotec.ecommerce.kafka;

import com.avro.PaymentNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
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

        CompletableFuture<SendResult<String, PaymentNotification>> completableFuture = kafkaTemplate.send(PAYMENT_TOPIC,paymentNotification);
        log.info("Sending completableFuture to kafka :: {}", completableFuture);

        completableFuture.whenComplete((result, exception) -> {
            log.info("Sending completableFuture result :: {}", result);
            if (exception != null) {
                log.error("Error sending payment notification to kafka :: {}", exception.getMessage());
            } else {
                log.info("Payment notification message : {} - sent to kafka successfully with offset : {}", paymentNotification, result.getRecordMetadata().offset());
            }
        });
    }
}
