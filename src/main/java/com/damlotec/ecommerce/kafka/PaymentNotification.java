package com.damlotec.ecommerce.kafka;

import com.damlotec.ecommerce.payment.PaymentMethod;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
@JsonDeserialize(using = PaymentNotificationDeserializer.class)
public record PaymentNotification (
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderRef,
        String customerFirstName,
        String customerLastName,
        String customerEmail
) {}

