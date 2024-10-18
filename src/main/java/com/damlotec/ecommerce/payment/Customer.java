package com.damlotec.ecommerce.payment;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record Customer(
            String id,
            @NotNull(message = "First name is required")
            String firstName,
            @NotNull(message = "Last name is required")
            String lastName,
            @NotNull(message = "Email is required")
            @Email(message = "Email is not valid")
            String email
) {
}
