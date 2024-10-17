package com.damlotec.ecommerce.product;

import com.damlotec.ecommerce.exceptions.ProuctNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProductController.class)
@Testcontainers
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProductService productService;

    private List<Product> products;

    @BeforeEach
    void setUp() {
        products = List.of(
                Product.builder().id(1).name("product1").description("product1").price(new BigDecimal(100)).availableQuantity(100).build(),
                Product.builder().id(2).name("product2").description("product2").price(new BigDecimal(200)).availableQuantity(200).build(),
                Product.builder().id(3).name("product3").description("product3").price(new BigDecimal(300)).availableQuantity(300).build()
        );

    }

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Test
    void productConnexionDb() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest("product", "product", new BigDecimal(100), 100, 1);

        when(productService.createProduct(any())).thenReturn(products.get(0).getId());
        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(String.valueOf(products.get(0).getId())));
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        List<ProductResponse> productResponses = List.of(
                new ProductResponse(1, "product1", "product1", new BigDecimal(100), 100, 1, "cat1", "cat1"),
                new ProductResponse(2, "product2", "product2", new BigDecimal(200), 200, 2, "cat2", "cat2"),
                new ProductResponse(3, "product3", "product3", new BigDecimal(300), 300, 3, "cat3", "cat3")
        );

        when(productService.getAllProducts()).thenReturn(productResponses);
        mockMvc.perform(get("/api/v1/products")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productResponses)))
                .andExpect(jsonPath("$.size()", Matchers.is(3)));

    }

    @Test
    void shouldGetProductById() throws Exception {
        Integer id = 1;
        ProductResponse productResponse = new ProductResponse(1, "product1", "product1", new BigDecimal(100), 100, 1, "cat1", "cat1");

        when(productService.getProduct(id)).thenReturn(productResponse);
        mockMvc.perform(get("/api/v1/products/{id}", id)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productResponse)));
    }

    @Test
    void shouldNotFoundProductById() throws Exception {
        Integer id = 9;
        when(productService.getProduct(any())).thenThrow(ProuctNotFoundException.class);
        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        int id = 1;
        ProductRequest productRequest = new ProductRequest("productUpdate", "product", new BigDecimal(100), 100, 1);

        when(productService.updateProduct(id, productRequest)).thenReturn(1);
        mockMvc.perform(patch("/api/v1/products/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(String.valueOf(1)));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        int id = 1;
        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldProductsPurchase() throws Exception {
        List<ProductPurchaseRequest> productPurchaseRequests = List.of(
                new ProductPurchaseRequest(1, 1),
                new ProductPurchaseRequest(2, 2),
                new ProductPurchaseRequest(3, 3)
        );
        List<ProductPurchaseResponse> productPurchaseResponses = List.of(
                new ProductPurchaseResponse(1, "product1", "product1", new BigDecimal(100), 1),
                new ProductPurchaseResponse(2, "product2", "product2", new BigDecimal(200), 2),
                new ProductPurchaseResponse(3, "product3", "product3", new BigDecimal(300), 3)
        );

        when(productService.ProductsPurchase(productPurchaseRequests)).thenReturn(productPurchaseResponses);
        mockMvc.perform(post("/api/v1/products/purchase")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(productPurchaseRequests)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productPurchaseRequests)))
                .andExpect(jsonPath("$.size()", Matchers.is(3)));
    }
}