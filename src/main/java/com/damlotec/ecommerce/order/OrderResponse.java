package com.damlotec.ecommerce.order;

import java.math.BigDecimal;

public record OrderResponse(
        String reference,
        PaymentMethod paymentMethod,
        BigDecimal totalAmount,
        String customerId
) {
}
