package com.damlotec.ecommerce.order;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapper();

    @Test
    void toOrder() {
        OrderRequest orderRequest = new OrderRequest("ref", new BigDecimal(100), PaymentMethod.CREDIT_CARD, "1", null);
        Order expected = Order.builder().customerId("1").reference("ref").paymentMethod(PaymentMethod.CREDIT_CARD).totalAmount(new BigDecimal(100)).build();
        Order result = mapper.toOrder(orderRequest);
        assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    void toOrderResponse() {
        Order order = Order.builder().customerId("1").reference("ref").paymentMethod(PaymentMethod.CREDIT_CARD).totalAmount(new BigDecimal(100)).build();
        OrderResponse expected = new OrderResponse("ref", PaymentMethod.CREDIT_CARD, new BigDecimal(100), "1");
        OrderResponse result = mapper.toOrderResponse(order);
        assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }
}