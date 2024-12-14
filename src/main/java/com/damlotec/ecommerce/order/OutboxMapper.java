package com.damlotec.ecommerce.order;

import com.damlotec.ecommerce.kafka.OrderConfirmation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OutboxMapper {

    @SneakyThrows
    public Outbox toOutbox(OrderConfirmation orderConfirmation) {
        return Outbox.builder()
                .aggregateId(orderConfirmation.getCustomer().getId())
                .messageType("ORDER")
                .payload(new ObjectMapper().writeValueAsString(orderConfirmation))
                .status(Boolean.FALSE)
                .createdAt(LocalDateTime.now())
                .build();
    }
    public OrderConfirmation toOrderConfirmation(Order order) {
        return OrderConfirmation.builder()
                .orderId(order.getId())
                .reference(order.getReference())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .build();
    }
}
