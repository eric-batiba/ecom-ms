package com.damlotec.ecommerce.email;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplateType {
    PAYMENT_CONFIRMATION("paymentConfirmation.html", "payment confirmation"),
    ORDER_CONFIRMATION("orderConfirmation.html", "payment confirmation");

    private final String template;
    private final String subject;
}
