package com.damlotec.ecommerce.orderline;

public record OrderLineRequest(
        Integer orderId,
        int productId,
        int quantity) {
}
