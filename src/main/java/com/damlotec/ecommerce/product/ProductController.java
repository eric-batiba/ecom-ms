package com.damlotec.ecommerce.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService service;

    @PostMapping
    public ResponseEntity<Integer> createProduct(@RequestBody @Valid ProductRequest request) {
        return ResponseEntity.status(CREATED).body(service.createProduct(request));
    }
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getProduct(id));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Integer> updateProduct(@PathVariable Integer id, @RequestBody @Valid ProductRequest request){
        return ResponseEntity.ok(service.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/purchase")
    public ResponseEntity<List<ProductPurchaseResponse>> productsPurchase(@RequestBody @Valid List<ProductPurchaseRequest> requests){
        return ResponseEntity.ok(service.ProductsPurchase(requests));
    }
}
