package com.damlotec.ecommerce.order;

import com.damlotec.ecommerce.customer.CustomerClient;
import com.damlotec.ecommerce.customer.CustomerResponse;
import com.damlotec.ecommerce.exception.BusinessException;
import com.damlotec.ecommerce.exception.OrderNotFoundException;
import com.damlotec.ecommerce.kafka.OrderConfirmation;
import com.damlotec.ecommerce.kafka.OrderProducer;
import com.damlotec.ecommerce.orderline.OrderLineRequest;
import com.damlotec.ecommerce.orderline.OrderLineService;
import com.damlotec.ecommerce.product.ProductClient;
import com.damlotec.ecommerce.product.ProductPurchaseRequest;
import com.damlotec.ecommerce.product.ProductPurchaseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerClient customerClient;
    @Mock
    private ProductClient productClient;
    @Mock
    private OrderLineService orderLineService;
    @Mock
    private OrderProducer orderProducer;
    @Mock
    private OrderMapper mapper;
    @InjectMocks
    private OrderService underTest;
    @Captor
    ArgumentCaptor<OrderConfirmation> orderConfirmationCaptor;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Test
    void testConnexionDb() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    OrderRequest orderRequest;
    CustomerResponse customerResponse;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderRequest(
                "order-ref",
                new BigDecimal(100),
                PaymentMethod.CREDIT_CARD,
                "1",
                List.of(new ProductPurchaseRequest(1, 5))
        );
        customerResponse = new CustomerResponse("1", "btb", "eric", "btb@gmail.com", null);
    }

    @Test
    void shouldCreateOrder() {
        //given
        List<ProductPurchaseResponse> productPurchaseResponses = List.of(new ProductPurchaseResponse(1, "product-1", "product-1", new BigDecimal(100), 5));
        Order order = Order.builder().id(1).build();
        //when
        when(customerClient.getCustomerById(anyString())).thenReturn(Optional.of(customerResponse));
        when(productClient.getPurchaseProducts(anyList())).thenReturn(productPurchaseResponses);
        when(mapper.toOrder(orderRequest)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Integer result = underTest.createOrder(orderRequest);

        //verify
        verify(orderLineService, times(1)).saveOrderLine(any(OrderLineRequest.class));
        verify(orderProducer, times(1)).sendOrderConfirmation(orderConfirmationCaptor.capture());
        OrderConfirmation sentOrderConfirmation = orderConfirmationCaptor.getValue();

        //then
        assertThat(result).isNotNull();
        assertThat(1).isEqualTo(result);
        assertThat("order-ref").isEqualTo(sentOrderConfirmation.reference());
    }

    @Test
    void shouldNotCreateOrderWhenCustomerNotFound() {
        when(customerClient.getCustomerById(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.createOrder(orderRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage(String.format("Cannot create Order:: No customer exist wit this ID %s", orderRequest.customerId()));
    }

    @Test
    void shouldNotCreateOrderWhenProductsPurchaseNotAvailable() {
        when(customerClient.getCustomerById(anyString())).thenReturn(Optional.ofNullable(customerResponse));
        when(productClient.getPurchaseProducts(anyList())).thenThrow(new BusinessException("Error while purchase products :: "));
        BusinessException exception = assertThrows(BusinessException.class, () -> underTest.createOrder(orderRequest));
        assertEquals("Error while purchase products :: ", exception.getMessage());
    }

    @Test
    void shouldFindAllOrder() {
        //given
        Order order = Order.builder().id(1).build();
        Order order1 = Order.builder().id(2).build();
        //when
        when(orderRepository.findAll()).thenReturn(List.of(order, order1));
        when(mapper.toOrderResponse(order)).thenReturn(new OrderResponse("ref-1", PaymentMethod.CREDIT_CARD, new BigDecimal(100), "1"));
        when(mapper.toOrderResponse(order1)).thenReturn(new OrderResponse("ref-2", PaymentMethod.CREDIT_CARD, new BigDecimal(200), "1"));

        List<OrderResponse> result = underTest.findAll();
        //then
        assertThat(result).hasSize(2);
        assertThat("ref-1").isEqualTo(result.getFirst().reference());
        assertThat("ref-2").isEqualTo(result.getLast().reference());
        //verify
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void shouldFindOrderById() {
        //given
        Integer id = 1;
        Order order = Order.builder().id(1).build();
        OrderResponse orderResponse = new OrderResponse("ref-1", PaymentMethod.CREDIT_CARD, new BigDecimal(100), "1");
        //when
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(order));
        when(mapper.toOrderResponse(order)).thenReturn(orderResponse);

        OrderResponse result = underTest.findById(id);
        //then
        assertThat(result).isNotNull();
        assertThat("ref-1").isEqualTo(result.reference());
        //verify
        verify(orderRepository, times(1)).findById(anyInt());
    }

    @Test
    void shouldNotFindOrderById() {
        Integer id = 5;
        when(orderRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.findById(id))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage(String.format("Cannot find Order with ID : %s", id));
    }
}