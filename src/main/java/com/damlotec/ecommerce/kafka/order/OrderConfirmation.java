package com.damlotec.ecommerce.kafka.order;

import com.damlotec.ecommerce.kafka.payment.PaymentMethod;

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
