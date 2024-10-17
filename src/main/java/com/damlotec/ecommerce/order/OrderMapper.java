package com.damlotec.ecommerce.order;

import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public Order toOrder(OrderRequest request) {
        return Order.builder()
                .customerId(request.customerId())
                .reference(request.reference())
                .paymentMethod(request.paymentMethod())
                .totalAmount(request.amount())
                .build();
    }

    public OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getReference(),
                order.getPaymentMethod(),
                order.getTotalAmount(),
                order.getCustomerId()
        );
    }
}
