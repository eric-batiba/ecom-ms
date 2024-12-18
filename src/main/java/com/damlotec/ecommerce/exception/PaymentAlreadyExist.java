package com.damlotec.ecommerce.exception;

public class PaymentAlreadyExist extends RuntimeException {
    public PaymentAlreadyExist(String message) {
        super(message);
    }
}
