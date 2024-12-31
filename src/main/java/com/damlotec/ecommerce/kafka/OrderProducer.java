package com.damlotec.ecommerce.kafka;

import com.avro.OrderConfirmation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static com.damlotec.ecommerce.config.OrderTopicConfig.ORDER_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {
    private final KafkaTemplate<String, OrderConfirmation> kafkaTemplate;

    public void sendOrderConfirmation(OrderConfirmation orderConfirmation) {
        log.info("Order confirmation sent to kafka: {}", orderConfirmation);

        CompletableFuture<SendResult<String, OrderConfirmation>> completableFuture = kafkaTemplate.send(ORDER_TOPIC, orderConfirmation);
        log.info("Send completableFuture: {}", completableFuture);
        completableFuture.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Error sending order confirmation to kafka : {}", exception.getMessage());
            } else {
                log.info("Order confirmation message : {} sent to kafka successfully with offset : {}", orderConfirmation, result.getRecordMetadata().offset());
            }
        });

    }
}
