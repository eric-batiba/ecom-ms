package com.damlotec.ecommerce.order;

import com.damlotec.ecommerce.customer.CustomerClient;
import com.damlotec.ecommerce.customer.CustomerResponse;
import com.damlotec.ecommerce.exception.BusinessException;
import com.damlotec.ecommerce.exception.OrderNotFoundException;
import com.damlotec.ecommerce.kafka.OrderConfirmation;
import com.damlotec.ecommerce.orderline.OrderLineRequest;
import com.damlotec.ecommerce.orderline.OrderLineService;
import com.damlotec.ecommerce.product.ProductClient;
import com.damlotec.ecommerce.product.ProductPurchaseRequest;
import com.damlotec.ecommerce.product.ProductPurchaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
//@Transactional
@Slf4j
public class OrderService implements IorderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderMapper mapper;
    private final OutboxMapper outboxMapper;

    @Override
    @Transactional
    public Integer createOrder(OrderRequest orderRequest) {
        // check if customer exists --> customer-ms
        CustomerResponse customerResponse = customerClient.getCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException(String.format("Cannot create Order:: No customer exist wit this ID %s", orderRequest.customerId())));
        List<ProductPurchaseResponse> purchaseProducts = productClient.getPurchaseProducts(orderRequest.products());
        Order order = orderRepository.save(mapper.toOrder(orderRequest));
        OrderConfirmation orderConfirmation = outboxMapper.toOrderConfirmation(order);
        orderConfirmation.setCustomer(customerResponse);
        orderConfirmation.setProducts(purchaseProducts);
        Outbox outbox = outboxMapper.toOutbox(orderConfirmation);
        outboxRepository.save(outbox);
        //save orderItems
        for (ProductPurchaseRequest productPurchaseRequest : orderRequest.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            order.getId(),
                            productPurchaseRequest.productId(),
                            productPurchaseRequest.quantity()
                    )
            );
        }

        return order.getId();
    }

    @Override
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(mapper::toOrderResponse)
                .toList();
    }

    @Override
    public OrderResponse findById(Integer id) {
        return orderRepository.findById(id)
                .map(mapper::toOrderResponse)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Cannot find Order with ID : %s", id)));
    }
}
