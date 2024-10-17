package com.damlotec.ecommerce;

import com.damlotec.ecommerce.order.OrderRequest;
import com.damlotec.ecommerce.order.OrderResponse;
import com.damlotec.ecommerce.order.PaymentMethod;
import com.damlotec.ecommerce.product.ProductPurchaseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
class OrderServiceApplicationTests {
	@Autowired
	TestRestTemplate testRestTemplate;
	@LocalServerPort
	int port;

	OrderRequest orderRequest;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		orderRequest = new OrderRequest(
				"order-ref",
				new BigDecimal(100),
				PaymentMethod.CREDIT_CARD,
				"1",
				List.of(new ProductPurchaseRequest(51, 1))
		);
	}

	@Container
	@ServiceConnection
	private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

	@Test
	void orderConnexionDb() {
		assertThat(postgreSQLContainer.isCreated()).isTrue();
		assertThat(postgreSQLContainer.isRunning()).isTrue();
	}

	@Container
	static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka:3.8.0"));

	@DynamicPropertySource
	public static void initKafkaProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
	}

	@Test
	void shouldCreateOrder() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		ResponseEntity<Integer> response = testRestTemplate.exchange(
				getUrl(),
				HttpMethod.POST,
				new HttpEntity<>(orderRequest, headers),
				Integer.class
		);
		System.out.println("Response Body: " + response.getBody());
		assertThat(response.getStatusCode()).isEqualTo(CREATED);
		assertThat(response.getBody()).isEqualTo(2);
	}

	@Test
	void shouldNotCreateOrderWhenOrderRequestIsInvalid() {
		OrderRequest request = new OrderRequest("oder-ref", new BigDecimal(-100), null, "", null);
		ResponseEntity<String> response = testRestTemplate.exchange(
				getUrl(),
				HttpMethod.POST,
				new HttpEntity<>(request),
				String.class
		);
		assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
	}


	@Test
	void shouldGetAllOrders() {
		ResponseEntity<List<OrderResponse>> response = testRestTemplate.exchange(
				getUrl(),
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<>() {
				}
		);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(OK);
	}

	@Test
	void shouldGetOrderById() {
		ResponseEntity<OrderResponse> response = testRestTemplate.exchange(
				getUrl() + "/2",
				HttpMethod.GET,
				null,
				OrderResponse.class
		);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(OK);
		assertThat(response.getBody().reference()).isEqualTo("order-ref");
	}


	private String getUrl() {
		return "http://localhost:" + port + "/api/v1/orders";
	}

}
