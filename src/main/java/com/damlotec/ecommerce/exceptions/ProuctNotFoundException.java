package com.damlotec.ecommerce.exceptions;

public class ProuctNotFoundException extends RuntimeException {
    public ProuctNotFoundException(String message) {
        super(message);
    }
}
