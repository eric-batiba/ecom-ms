package com.damlotec.ecommerce.product;

import com.damlotec.ecommerce.exception.BusinessException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductClient {
//    @Value("${application.config.product-url}")
//    String productUrl;

    private final RestTemplate restTemplate;

//    @CircuitBreaker(name = "productService", fallbackMethod = "getPurchaseProductsFallback")
    public List<ProductPurchaseResponse> getPurchaseProducts(List<ProductPurchaseRequest> requests) {
        log.info("Getting purchase products from product service");
        HttpHeaders header = new HttpHeaders();
        header.set(CONTENT_TYPE, APPLICATION_JSON);

        ResponseEntity<List<ProductPurchaseResponse>> response = restTemplate.exchange(
                 "http://PRODUCT-SERVICE/api/v1/products/purchase",
                HttpMethod.POST,
                new HttpEntity<>(requests, header),
                new ParameterizedTypeReference<>() {
                }
        );
        if (response.getStatusCode().isError())
            throw new BusinessException("Error while purchase products :: " + response.getStatusCode());
        return response.getBody();
    }

    List<ProductPurchaseResponse>  getPurchaseProductsFallback(List<ProductPurchaseRequest> requests, Exception e) {
        log.info("Getting purchase products from product service fallback triggered due to : {} ", e.getMessage());
        return List.of(new ProductPurchaseResponse(
                0,
                "Product not available",
                "Product not available",
                new BigDecimal(0),
                0
        ));
    }
}
