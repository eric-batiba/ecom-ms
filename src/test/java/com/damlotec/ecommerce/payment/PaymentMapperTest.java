package com.damlotec.ecommerce.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMapperTest {
    private final PaymentMapper mapper = new PaymentMapper();

    @Test
    void toPayment() {
        PaymentRequest paymentRequest = new PaymentRequest(new BigDecimal(100), PaymentMethod.CREDIT_CARD, 1, "order-ref", null);
        Payment expected = Payment.builder().id(1).totalAmount(new BigDecimal(100)).paymentMethod(PaymentMethod.CREDIT_CARD).orderId(1).build();
        Payment result = mapper.toPayment(paymentRequest);
        assertThat(expected).usingRecursiveComparison().ignoringFields("customer","id", "orderRef").isEqualTo(result);
    }
}