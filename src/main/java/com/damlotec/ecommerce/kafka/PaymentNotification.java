package com.damlotec.ecommerce.kafka;

import com.damlotec.ecommerce.payment.PaymentMethod;

import java.math.BigDecimal;

public record PaymentNotification (
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderRef,
        String customerFirstName,
        String customerLastName,
        String customerEmail
) {}
