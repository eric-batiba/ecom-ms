package com.damlotec.ecommerce.customer;

public record Customer(
        String id,
        String firstName,
        String lastName,
        String email
) {
}
