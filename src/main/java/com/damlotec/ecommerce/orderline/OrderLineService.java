package com.damlotec.ecommerce.orderline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderLineService implements IorderLineService {
    private final OrderLineRepository orderLineRepository;
    private final OrderLineMapper orderLineMapper;

    @Override
    public Integer saveOrderLine(OrderLineRequest orderLineRequest) {
        log.info("Saving order line: {}", orderLineRequest);
        OrderItem orderLine = orderLineMapper.toOrderLine(orderLineRequest);
        return orderLineRepository.save(orderLine).getId();
    }

    @Override
    public List<OrderLineResponse> getAllOrderLineByOderId(Integer orderId) {
        log.info("Getting order line for order id: {}", orderId);
        return orderLineRepository.findAllByOrderId(orderId)
                .stream()
                .map(orderLineMapper::toOrderLineResponse)
                .toList();
    }
}
