package com.damlotec.ecommerce;

import com.damlotec.ecommerce.order.Order;
import com.damlotec.ecommerce.order.OrderRepository;
import com.damlotec.ecommerce.order.PaymentMethod;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.math.BigDecimal;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
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
