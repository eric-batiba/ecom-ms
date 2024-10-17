package com.damlotec.ecommerce;

import com.damlotec.ecommerce.config.ConfigParams;
import com.damlotec.ecommerce.customer.Customer;
import com.damlotec.ecommerce.customer.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties({ConfigParams.class})
@Slf4j
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
        return args -> {
            log.info("---------SAVE CUSTOMER--------------");
            Customer customer = Customer.builder()
                    .id("1")
                    .email("btb@gmail.com")
                    .firstName("btb")
                    .lastName("eric")
                    .build();
            Customer savedCustomer = customerRepository.save(customer);
            log.info("saved customer {} ", savedCustomer);
            log.info("-----------------------");

        };
    }

}
