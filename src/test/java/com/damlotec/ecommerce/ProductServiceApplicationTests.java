package com.damlotec.ecommerce;

import com.damlotec.ecommerce.product.ProductRequest;
import com.damlotec.ecommerce.product.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
class ProductServiceApplicationTests {
    @Autowired
    TestRestTemplate testRestTemplate;
    @LocalServerPort
    int port;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Test
    void productConnexionDb() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }


    @Test
    void shouldCreateProduct() {
        //Arrange
        ProductRequest productRequest = new ProductRequest("test", "test", new BigDecimal(100), 10, 1);
        //Act
        ResponseEntity<Integer> response = testRestTemplate.postForEntity(getUrl(), productRequest, Integer.class);
        //Assert
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldNotCreateProductWithInvalidField() {
        //Arrange
        ProductRequest productRequest = new ProductRequest(null, "", new BigDecimal(100), 10, 1);
        //Act
        ResponseEntity<String> response = testRestTemplate.exchange(getUrl()
                , HttpMethod.POST
                , new HttpEntity<>(productRequest)
                , String.class
        );
        //Assert
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void shouldGetAllProducts() {
        //Arrange
        //Act
        ResponseEntity<List<ProductResponse>> response = testRestTemplate.exchange(getUrl()
                , HttpMethod.GET
                , null
                , new ParameterizedTypeReference<>() {
                }
        );
        //Assert
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();

    }

    @Test
    void shouldGetProductById() {
        //Arrange
        Integer productId = 452;
        //Act
        ResponseEntity<ProductResponse> response = testRestTemplate.getForEntity(getUrl() + "/" + productId, ProductResponse.class);
        //Assert
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(productId);
    }

    @Test
    void shouldUpdateProduct() {
        //Arrange
        Integer productId = 452;
        ProductRequest productRequestUpdate = new ProductRequest("test2", "test2", new BigDecimal(200), 20, 1);
        //Act
        ResponseEntity<Integer> response = testRestTemplate.exchange(getUrl() + "/" + productId, HttpMethod.PATCH, new HttpEntity<>(productRequestUpdate), Integer.class, productId);
        //Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(productId);
    }

    @Test
    void shouldDeleteProduct() {
        //Arrange
        Integer productId = 51;

        ResponseEntity<Void> response = testRestTemplate.exchange(getUrl() + "/" + productId, HttpMethod.DELETE, null, Void.class, productId);
        assertThat(response.getBody()).isNull();
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    private String getUrl() {
        return "http://localhost:" + port + "/api/v1/products";
    }
}
