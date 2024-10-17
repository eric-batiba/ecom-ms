package com.damlotec.ecommerce.customer;

import java.util.List;

public interface Icustomer {
    String createCustomer(CustomerRequest customerRequest);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse getCustomerById(String id);

    String deleteCustomerById(String id);

    Boolean isCustomerExist(String customerId);

    CustomerResponse  getCustomerByEmail(String email);

    Boolean isCustomerExistByEmail(String email);

    String updateCustomer(String id, CustomerRequest request);
}
