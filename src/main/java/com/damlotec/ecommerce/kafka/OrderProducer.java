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
    private final KafkaTemplate<String, OrderConfirmation> kafkaTemplate;

    public void sendOrderConfirmation(OrderConfirmation orderConfirmation) {
        log.info("Order confirmation sent to kafka");
        Message<OrderConfirmation> message = MessageBuilder
                .withPayload(orderConfirmation)
                .setHeader(TOPIC, ORDER_TOPIC)
                .build();
        CompletableFuture<SendResult<String, OrderConfirmation>> completableFuture = kafkaTemplate.send(message);
        completableFuture.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Error sending order confirmation to kafka : {}", exception.getMessage());
            } else {
                log.info(String.format("Order confirmation message : %s sent to kafka successfully with offset : %d", message, result.getRecordMetadata().offset()));
            }
        });

    }
}
