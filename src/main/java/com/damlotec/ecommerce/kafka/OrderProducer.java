package com.damlotec.ecommerce.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static com.damlotec.ecommerce.config.OrderTopicConfig.ORDER_TOPIC;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrderConfirmation(String payload) {
        log.info("Order confirmation sent to kafka");
        Message<String> message = MessageBuilder
                .withPayload(payload)
                .setHeader(TOPIC, ORDER_TOPIC)
                .build();
        CompletableFuture<SendResult<String, String>> completableFuture = kafkaTemplate.send(message);
        completableFuture.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Error sending order confirmation to kafka : {}", exception.getMessage());
            } else {
                log.info("Order confirmation message : {} sent to kafka successfully with offset : {}", message, result.getRecordMetadata().offset());
            }
        });

    }
}
