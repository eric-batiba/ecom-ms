package com.damlotec.ecommerce.handler;

import com.damlotec.ecommerce.exceptions.CategoryNotFoundException;
import com.damlotec.ecommerce.exceptions.InsufficientQuanityException;
import com.damlotec.ecommerce.exceptions.ProductPurchaseException;
import com.damlotec.ecommerce.exceptions.ProuctNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler({CategoryNotFoundException.class, ProuctNotFoundException.class, ProductPurchaseException.class})
    public ProblemDetail handleException(Exception ex) {
        return ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({InsufficientQuanityException.class})
    public ProblemDetail handleExistException(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handlerMethodArgumentException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return ResponseEntity.status(BAD_REQUEST).body(errors);
    }

}
