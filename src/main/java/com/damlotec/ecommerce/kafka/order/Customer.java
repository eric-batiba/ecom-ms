package com.damlotec.ecommerce.kafka.order;

public record Customer(
         String id,
         String firstName,
         String lastName,
         String email,
         Address address
) {
}
