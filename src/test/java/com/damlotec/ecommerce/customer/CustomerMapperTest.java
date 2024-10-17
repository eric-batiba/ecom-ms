package com.damlotec.ecommerce.customer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerMapperTest {

    private CustomerMapper customerMapper = new CustomerMapper();

    @Test
    void toCustomer() {

        CustomerRequest request = CustomerRequest.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .build();
        Customer expected = Customer.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .build();
        Customer result = customerMapper.toCustomer(request);
        assertThat(expected).usingRecursiveComparison().isEqualTo(result);

    }

    @Test
    void toCustomerResponse() {
        Customer customer = Customer.builder()
                .id("1")
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .build();
        CustomerResponse expected = CustomerResponse.builder()
                .id("1")
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .build();
        CustomerResponse result = customerMapper.toCustomerResponse(customer);
        assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }


    @Test
    void shouldNotMapNullCustomerToCustomerDTO() {
        assertThatThrownBy(
                ()->customerMapper.toCustomer(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}