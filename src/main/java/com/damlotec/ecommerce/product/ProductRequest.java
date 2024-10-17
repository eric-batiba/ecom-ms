package com.damlotec.ecommerce.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(
        @NotNull(message = "name is required")
        @NotBlank(message = " name cannot be empty")
        String name,
        @NotNull(message = "description is required")
        String description,
        @Positive(message = "price must be positive")
        BigDecimal price,
        @Positive(message = "availableQuantity must be positive")
        double availableQuantity,
        @NotNull(message = "categoryId is required")
        Integer categoryId
) {
}
