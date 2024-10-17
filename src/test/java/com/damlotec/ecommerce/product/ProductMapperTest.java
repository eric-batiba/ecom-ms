package com.damlotec.ecommerce.product;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {
    private final ProductMapper mapper = new ProductMapper();

    @Test
    void toEntity() {
        ProductRequest request = new ProductRequest("name", "description", new BigDecimal(100), 100.0, 1);
        Product expected = Product.builder().name("name").description("description").price(new BigDecimal(100)).availableQuantity(100).category(Category.builder().id(request.categoryId()).build()).build();
        Product result = mapper.toEntity(request);
        assertThat(expected).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);

    }

    @Test
    void toResponse() {
        Category category = Category.builder().id(1).name("name").build();
        Product product = Product.builder().id(1).name("name").description("description").price(new BigDecimal(100)).availableQuantity(100).category(category).build();
        ProductResponse expected = new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getAvailableQuantity(), product.getCategory().getId(), product.getCategory().getName(), category.getDescription());
        ProductResponse result = mapper.toResponse(product);
        assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    void toProductPurchaseResponse() {
        int quantity = 1;
        Product product = Product.builder().id(1).name("name").description("description").price(new BigDecimal(100)).availableQuantity(100).build();
        ProductPurchaseResponse expected = new ProductPurchaseResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(),quantity);
        ProductPurchaseResponse result = mapper.toProductPurchaseResponse(product, quantity);
        assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }
}