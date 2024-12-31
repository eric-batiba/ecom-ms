package com.damlotec.ecommerce.payment;

import com.avro.OrderConfirmation;
import com.damlotec.ecommerce.customer.Customer;
import com.damlotec.ecommerce.order.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentMapper {
    public Customer toCustomer(com.avro.Customer avroCustomer) {
        if (avroCustomer == null) {
            throw new IllegalArgumentException("Customer data is null");
        }
        return new Customer(
                avroCustomer.getId().toString(),
                avroCustomer.getFirstName().toString(),
                avroCustomer.getLastName().toString(),
                avroCustomer.getEmail().toString()
        );
    }

    public PaymentRequest toPaymentRequest(OrderConfirmation confirmation) {
        return new PaymentRequest(
                BigDecimal.valueOf(confirmation.getTotalAmount()),
                PaymentMethod.valueOf(confirmation.getPaymentMethod().toString()),
                confirmation.getOrderId(),
                confirmation.getReference() != null ? confirmation.getReference().toString() : null,
                toCustomer(confirmation.getCustomer())

        );
    }
}
