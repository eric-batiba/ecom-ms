package com.damlotec.ecommerce.product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "product-service", url = "http://localhost:8082/api/v1/products")
public interface ProductClient2 {
    @PostMapping("/purchase")
    List<ProductPurchaseResponse> productsPurchase(List<ProductPurchaseRequest> products);
}
