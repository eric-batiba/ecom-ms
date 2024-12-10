package com.damlotec.ecommerce.kafka.order;

public record Address(
        String street,
        int houseNumber,
        String zipCode
) {
}
