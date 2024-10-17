package com.damlotec.ecommerce.customer;

import com.damlotec.ecommerce.order.Address;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CustomerResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Address address;
}
