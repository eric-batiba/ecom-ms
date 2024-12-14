package com.damlotec.ecommerce.kafka;

import com.damlotec.ecommerce.customer.CustomerResponse;
import com.damlotec.ecommerce.order.PaymentMethod;
import com.damlotec.ecommerce.product.ProductPurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        Integer orderId,
        String reference,
        PaymentMethod paymentMethod,
        BigDecimal totalAmount,
        CustomerResponse customer,
        List<ProductPurchaseResponse> products
) {
}
