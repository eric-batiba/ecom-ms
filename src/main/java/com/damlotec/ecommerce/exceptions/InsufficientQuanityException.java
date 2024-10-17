package com.damlotec.ecommerce.exceptions;

public class InsufficientQuanityException extends RuntimeException {
    public InsufficientQuanityException(String message) {
        super(message);
    }
}
