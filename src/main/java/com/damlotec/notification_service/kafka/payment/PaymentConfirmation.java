package com.damlotec.notification_service.kafka.payment;

import java.math.BigDecimal;

public record PaymentConfirmation(
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderRef,
        String customerFirstName,
        String customerLastName,
        String customerEmail
) {
}
