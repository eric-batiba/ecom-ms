package com.damlotec.ecommerce.order;

import com.damlotec.ecommerce.exception.OrderNotFoundException;
import com.damlotec.ecommerce.product.ProductPurchaseRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(OrderController.class)
@Testcontainers
@AutoConfigureDataJpa
class OrderControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    OrderService orderService;
    @Autowired
    ObjectMapper objectMapper;

    OrderRequest orderRequest;
    List<OrderResponse> response;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderRequest(
                "order-ref",
                new BigDecimal(100),
                PaymentMethod.CREDIT_CARD,
                "1",
                List.of(new ProductPurchaseRequest(1, 5))
        );
        response = List.of(new OrderResponse("order-ref", PaymentMethod.CREDIT_CARD, new BigDecimal(100), "1"));
    }

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Test
    void orderConnexionDb() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }


    @Test
    void shouldCreateOrder() throws Exception {
        Order order = Order.builder().id(1).build();
        when(orderService.createOrder(orderRequest)).thenReturn(order.getId());
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(String.valueOf(order.getId())));
//                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(order.getId()));
    }

    @Test
    void shouldReturnOrders() throws Exception {
        when(orderService.findAll()).thenReturn(response);
        mockMvc.perform(get("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(response)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)));
    }

    @Test
    void shouldReturnOrderWhenGivenCorrectId() throws Exception {
        Integer id = 1;
        when(orderService.findById(id)).thenReturn(response.getFirst());
        mockMvc.perform(get("/api/v1/orders/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(response.getFirst())));
    }

    @Test
    void shouldReturnOrderWhenGivenWrongId() throws Exception {
        Integer id = 9;
        when(orderService.findById(id)).thenThrow(OrderNotFoundException.class);
        mockMvc.perform(get("/api/v1/orders/" + id))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}