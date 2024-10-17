package com.damlotec.ecommerce.product;

import jakarta.validation.constraints.Positive;

public record ProductPurchaseRequest(
        @Positive(message = "Product id must be positive")
        int productId,
        @Positive(message = "Quantity must be positive")
        int quantity
) {
}
