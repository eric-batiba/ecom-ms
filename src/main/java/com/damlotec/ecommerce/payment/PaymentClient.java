package com.damlotec.ecommerce.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "payment-service", url = "http://localhost:8084/api/v1/payments")
public interface PaymentClient {
    @PostMapping
    Integer pay(PaymentRequest paymentRequest);
}
