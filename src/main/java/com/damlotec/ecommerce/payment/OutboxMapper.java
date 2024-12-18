package com.damlotec.ecommerce.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OutboxMapper {

    @SneakyThrows
    public Outbox toOutbox(PaymentRequest paymentRequest) {
        return Outbox.builder()
                .aggregateId(paymentRequest.customer().id())
                .messageType("PAYMENT")
                .payload(new ObjectMapper().writeValueAsString(paymentRequest))
                .status(Boolean.FALSE)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
