package com.damlotec.ecommerce.order;

import com.damlotec.ecommerce.customer.CustomerClient;
import com.damlotec.ecommerce.customer.CustomerResponse;
import com.damlotec.ecommerce.exception.BusinessException;
import com.damlotec.ecommerce.exception.OrderNotFoundException;
import com.damlotec.ecommerce.kafka.OrderConfirmation;
import com.damlotec.ecommerce.kafka.OrderProducer;
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
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final OrderMapper mapper;

    //    private final ProductClient2 productClient;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createOrder(OrderRequest orderRequest) {
        // check if customer exists --> customer-ms
        CustomerResponse customerResponse = customerClient.getCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException(String.format("Cannot create Order:: No customer exist wit this ID %s", orderRequest.customerId())));
        //check if products purchase exist and are available quantity --> product-ms
        //productClient.productsPurchase(orderRequest.products()); // --> with OpenFeign
        List<ProductPurchaseResponse> purchaseProducts = productClient.getPurchaseProducts(orderRequest.products());// --> with RestTemplate

        //save order
        Order order = mapper.toOrder(orderRequest);
//        order.setId(2);
        Order orderSaved = orderRepository.save(order);

        //save orderItems
        for (ProductPurchaseRequest productPurchaseRequest : orderRequest.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            orderSaved.getId(),
                            productPurchaseRequest.productId(),
                            productPurchaseRequest.quantity()
                    )
            );
        }

//        TODO start the payment process --> payment-ms

        //send order-confirmation into kafka --> notification-ms
            orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        order.getId(),
                        orderRequest.reference(),
                        orderRequest.paymentMethod(),
                        orderRequest.amount(),
                        customerResponse,
                        purchaseProducts
                )
        );

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
