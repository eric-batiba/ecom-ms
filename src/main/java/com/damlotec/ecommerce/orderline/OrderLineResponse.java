package com.damlotec.ecommerce.orderline;

public record OrderLineResponse(
        Integer orderId,
        double quantity
) {
}
