package com.damlotec.ecommerce;

import com.damlotec.ecommerce.payment.Customer;
import com.damlotec.ecommerce.payment.PaymentMethod;
import com.damlotec.ecommerce.payment.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
class PaymentServiceApplicationTests {

	@Autowired
	TestRestTemplate testRestTemplate;
	@LocalServerPort
	int port;

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
	void shouldCreatePayment() {
		PaymentRequest paymentRequest = new PaymentRequest(new BigDecimal(100), PaymentMethod.CREDIT_CARD, 1, "order-ref", new Customer("1", "btb", "eric", "btb@gmail.com"));
		ResponseEntity<Integer> response = testRestTemplate.postForEntity(
				getUrl(),
				paymentRequest,
				Integer.class
		);
		assertThat(response.getStatusCode()).isEqualTo(CREATED);
		assertThat(response.getBody()).isEqualTo(1);
	}

	private String getUrl(){
		return "http://localhost:" + port + "/api/v1/payments";
	}

}
