package com.damlotec.ecommerce.orderline;

import com.damlotec.ecommerce.order.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderLineMapper {
    public OrderItem toOrderLine(OrderLineRequest request) {
        return OrderItem.builder()
                .order(Order.builder().id(request.orderId()).build())
                .productId(request.productId())
                .quantity(request.quantity())
                .build();
    }

    public OrderLineResponse toOrderLineResponse(OrderItem orderItem) {
        return new OrderLineResponse(
                orderItem.getId(),
                orderItem.getQuantity()
        );
    }
}
