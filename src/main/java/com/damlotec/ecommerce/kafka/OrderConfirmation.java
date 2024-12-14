package com.damlotec.ecommerce.kafka;

import com.damlotec.ecommerce.customer.CustomerResponse;
import com.damlotec.ecommerce.order.PaymentMethod;
import com.damlotec.ecommerce.product.ProductPurchaseResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public  class OrderConfirmation {
    private  Integer orderId;
    private  String reference;
    private  PaymentMethod paymentMethod;
    private  BigDecimal totalAmount;
    private  CustomerResponse customer;
    private  List<ProductPurchaseResponse> products;
}
