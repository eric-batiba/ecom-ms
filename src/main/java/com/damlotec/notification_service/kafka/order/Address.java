package com.damlotec.notification_service.kafka.order;

public record Address(
        String street,
        int houseNumber,
        String zipCode
) {
}
