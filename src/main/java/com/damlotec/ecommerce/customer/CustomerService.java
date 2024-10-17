package com.damlotec.ecommerce.customer;

import com.damlotec.ecommerce.exception.CustomerAlreadyExist;
import com.damlotec.ecommerce.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService implements Icustomer {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    @Override
    public String createCustomer(CustomerRequest request) {
        log.info("Creating customer {}", request);
        if (Boolean.TRUE.equals(customerRepository.existsByEmail(request.email()))) throw new CustomerAlreadyExist(format("Customer already exists with email: %s", request.email()));
        Customer customer = mapper.toCustomer(request);
        Customer savedCustomer = customerRepository.save(customer);
        return savedCustomer.getId();
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        log.info("Getting all customers");
        return customerRepository.findAll().stream()
                .map(mapper::toCustomerResponse)
                .toList();
    }

    @Override
    public CustomerResponse getCustomerById(String id) {
        log.info("Getting customer with id {}", id);
        return findCustomerById(id);
    }

    @Override
    public String deleteCustomerById(String id) {
        log.info("Deleting customer with id {}", id);
        CustomerResponse customerResponse = findCustomerById(id);
        return customerResponse.getId();
    }


    @Override
    public Boolean isCustomerExist(String customerId) {
        log.info("Checking if customer with id {} exists", customerId);
        return customerRepository.findById(customerId).isPresent();
    }

    @Override
    public CustomerResponse  getCustomerByEmail(String email) {
        log.info("Getting customer with email {}", email);
        return customerRepository.findByEmail(email)
                .map(mapper::toCustomerResponse)
                .orElseThrow(() -> new CustomerNotFoundException(format("Customer not found with email: %s", email)));
    }
    @Override
    public Boolean isCustomerExistByEmail(String email) {
        log.info("Checking if customer with email {} exists", email);
        return customerRepository.existsByEmail(email);
    }

    @Override
    public String updateCustomer(String id, CustomerRequest request) {
        log.info("Updating customer with id {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(format("Customer not found with id: %s", id)));
        customer.setId(customer.getId());
        return customerRepository.save(customer).getId();
    }

    private CustomerResponse findCustomerById(String id) {
        log.info("inside findCustomerById");
        return customerRepository.findById(id)
                .map(mapper::toCustomerResponse)
                .orElseThrow(() -> new CustomerNotFoundException(format("Customer not found with id: %s", id)));
    }

}
