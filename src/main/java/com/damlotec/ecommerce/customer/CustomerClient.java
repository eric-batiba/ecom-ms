package com.damlotec.ecommerce.customer;

import com.damlotec.ecommerce.order.Address;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClient {
    @GetMapping("/api/v1/customers/{id}")
    Optional<CustomerResponse> getCustomerById(@PathVariable String id);

//    @CircuitBreaker(name = "customerService", fallbackMethod = "getCustomerByIdFallback")
//    @Cacheable(value = "customers", key = "#id")

    default Optional<CustomerResponse> getCustomerByIdFallback(String id, Exception e) {
        System.out.println("Customer service unavailable, returning cached data if available: " + e.getMessage());
        return getFromCache(id);
    }

    @Cacheable(value = "customers", key = "#id")
    default Optional<CustomerResponse> getFromCache(String id) {
        // If this method is called and data is present in the cache, Spring Cache will return the cached value.
        // If no cached data is available, it will return an empty Optional.
        System.out.println("Fetching customer with id: " + id + " from cache.");
        return Optional.of(
                new CustomerResponse(
                        id,
                        "firstName not found",
                        "lastName not found",
                        "email not found",
                        Address.builder().houseNumber(0).street("street").zipCode("zipCode").build()
                ));
    }
}
