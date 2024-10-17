package com.damlotec.ecommerce.customer;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@Testcontainers
@DataMongoTest
class CustomerRepositoryTest {

    Customer customer;

    @Autowired
    CustomerRepository customerRepository;


    // Declare the MongoDB Test container
    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    // DynamicPropertySource allows us to override the application properties at runtime
    @DynamicPropertySource
    static void mongoDbProperties(@NotNull DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customerRepository.save(customer);
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    void testFindByEmail() {
        Optional<Customer> foundCustomer = customerRepository.findByEmail(customer.getEmail());
        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get()).usingRecursiveComparison().ignoringFields("id").isEqualTo(customer);
    }

    @Test
    void testExistsByEmail() {
        Boolean exists = customerRepository.existsByEmail(customer.getEmail());
        assertThat(exists).isTrue();
    }

    @Test
    void testFindByEmail_NotFound() {
        Optional<Customer> foundCustomer = customerRepository.findByEmail("notfound@example.com");
        assertThat(foundCustomer).isNotPresent();
    }

    @Test
    void testExistsByEmail_NotFound() {
        Boolean exists = customerRepository.existsByEmail("nonexistent@example.com");
        assertThat(exists).isFalse();
    }
}