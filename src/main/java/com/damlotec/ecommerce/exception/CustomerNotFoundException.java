package com.damlotec.ecommerce.exception;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class CustomerNotFoundException extends RuntimeException {
    public final String message;
}
