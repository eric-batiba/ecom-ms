package com.damlotec.ecommerce;

import org.springframework.boot.SpringApplication;

public class TestPaymentPollerApplication {

	public static void main(String[] args) {
		SpringApplication.from(PaymentPollerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
