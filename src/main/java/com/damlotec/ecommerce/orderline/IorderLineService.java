package com.damlotec.ecommerce.orderline;

import java.util.List;

public interface IorderLineService {
    Integer saveOrderLine(OrderLineRequest orderLineRequest);

    List<OrderLineResponse> getAllOrderLineByOderId(Integer orderId);
}
