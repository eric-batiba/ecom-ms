package com.damlotec.notification_service.kafka.order;

import java.math.BigDecimal;

public record Product(
        int productId,
        String name,
        String description,
        BigDecimal price,
        int quantity
) {
}
