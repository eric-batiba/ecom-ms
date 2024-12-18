package com.damlotec.ecommerce.exception;

public class PaymentFailException extends RuntimeException {
    public PaymentFailException(String message) {
        super(message);
    }
}
