package com.damlotec.notification_service.kafka.order;

import com.damlotec.notification_service.kafka.payment.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        Integer orderId,
        String reference,
        PaymentMethod paymentMethod,
        BigDecimal totalAmount,
        Customer customer,
        List<Product> products
) {
}
