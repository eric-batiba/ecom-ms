package com.damlotec.ecommerce.order;

import java.util.List;

public interface IorderService {
    Integer createOrder(OrderRequest orderRequest);

    List<OrderResponse> findAll();

    OrderResponse findById(Integer id);
}
