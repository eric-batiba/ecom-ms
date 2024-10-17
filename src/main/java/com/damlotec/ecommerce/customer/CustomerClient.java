package com.damlotec.ecommerce.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Service
@FeignClient(name = "customer-service", url = "http://localhost:8081/api/v1/customers")
public interface CustomerClient {
    @GetMapping("/{id}")
    Optional<CustomerResponse> getCustomerById(@PathVariable String id);
}
