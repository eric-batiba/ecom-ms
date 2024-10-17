package com.damlotec.ecommerce.kafka;

import com.damlotec.ecommerce.customer.CustomerResponse;
import com.damlotec.ecommerce.order.PaymentMethod;
import com.damlotec.ecommerce.product.ProductPurchaseResponse;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderProducerTest {
    @Autowired
    OrderProducer orderProducer;

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka:3.8.0"));
//            .asCompatibleSubstituteFor("apache/kafka"));

    @DynamicPropertySource
    public static void initKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
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
    void sendOrderConfirmation() {
        CustomerResponse customerResponse = new CustomerResponse("1", "btb", "eric", "btb@gmail.com", null);
        List<ProductPurchaseResponse> purchaseResponses = List.of(new ProductPurchaseResponse(51, "product-1", "product-1", new BigDecimal(100), 1));
        OrderConfirmation orderConfirmation = new OrderConfirmation(1, "order-ref", PaymentMethod.CREDIT_CARD, new BigDecimal(100), customerResponse, purchaseResponses);
        orderProducer.sendOrderConfirmation(orderConfirmation);
        Awaitility.await().pollInterval(Duration.ofSeconds(3))
                .atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
                });

    }
}