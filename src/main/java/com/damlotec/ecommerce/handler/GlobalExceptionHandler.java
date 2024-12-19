package com.damlotec.ecommerce.handler;

import com.damlotec.ecommerce.exception.PaymentAlreadyExist;
import com.damlotec.ecommerce.exception.PaymentFailException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({PaymentAlreadyExist.class})
    public ProblemDetail handler(Exception ex) {
        return ProblemDetail.forStatusAndDetail(CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({PaymentFailException.class})
    public ProblemDetail handlerException(Exception ex) {
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handler(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return ResponseEntity.status(BAD_REQUEST).body(errors);
    }
}
