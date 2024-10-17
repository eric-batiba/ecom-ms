package com.damlotec.ecommerce.product;

import com.damlotec.ecommerce.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public List<ProductPurchaseResponse> getPurchaseProducts(List<ProductPurchaseRequest> requests) {
        log.info("Getting purchase products from product service");
        HttpHeaders header = new HttpHeaders();
        header.set(CONTENT_TYPE, APPLICATION_JSON);

        ResponseEntity<List<ProductPurchaseResponse>> response = restTemplate.exchange(
                 "http://localhost:8082/api/v1/products/purchase",
                HttpMethod.POST,
                new HttpEntity<>(requests, header),
                new ParameterizedTypeReference<>() {
                }
        );
        if (response.getStatusCode().isError())
            throw new BusinessException("Error while purchase products :: " + response.getStatusCode());
        return response.getBody();
    }
}
