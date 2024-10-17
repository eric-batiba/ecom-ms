package com.damlotec.ecommerce.order;

import com.damlotec.ecommerce.product.ProductPurchaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        String reference,
        @Positive(message = "amount must be a positive number")
        BigDecimal amount,
        @NotNull(message = "payment method is required")
        PaymentMethod paymentMethod,
        @NotNull(message = "customer id is not null")
         @NotBlank(message = "customer id is not blank")
        String customerId,
        @NotEmpty(message = "you should purchase at least one product")
        List<ProductPurchaseRequest> products
) {
}
