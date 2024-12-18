package com.damlotec.ecommerce.payment;

import com.damlotec.ecommerce.customer.CustomerResponse;
import com.damlotec.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderRef,
        CustomerResponse customer
) {
}
