package com.damlotec.ecommerce;

import com.damlotec.ecommerce.config.OrderConfig;
import com.damlotec.ecommerce.order.Order;
import com.damlotec.ecommerce.order.OrderRepository;
import com.damlotec.ecommerce.order.PaymentMethod;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.math.BigDecimal;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
//@EnableCaching
@EnableConfigurationProperties(OrderConfig.class)
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

//	@Profile("test")
//	@Bean
	CommandLineRunner  commandLineRunner(OrderRepository orderRepository) {
		return args -> {
			System.out.println("--------SAVE ORDER------------");
			Order order = Order.builder()
//					.id(1)
					.reference("order-ref")
					.paymentMethod(PaymentMethod.CREDIT_CARD)
					.totalAmount(new BigDecimal(100))
					.customerId("1")
					.build();
			orderRepository.save(order);
			System.out.println("--------------------");

        };
	}

}
