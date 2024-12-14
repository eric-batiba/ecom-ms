package com.damlotec.ecommerce.order;

import com.damlotec.ecommerce.customer.CustomerClient;
import com.damlotec.ecommerce.customer.CustomerResponse;
import com.damlotec.ecommerce.exception.BusinessException;
import com.damlotec.ecommerce.exception.OrderNotFoundException;
import com.damlotec.ecommerce.kafka.OrderConfirmation;
import com.damlotec.ecommerce.kafka.OrderProducer;
import com.damlotec.ecommerce.orderline.OrderLineRequest;
import com.damlotec.ecommerce.orderline.OrderLineService;
import com.damlotec.ecommerce.payment.PaymentClient;
import com.damlotec.ecommerce.payment.PaymentRequest;
import com.damlotec.ecommerce.product.ProductClient;
import com.damlotec.ecommerce.product.ProductPurchaseRequest;
import com.damlotec.ecommerce.product.ProductPurchaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final PaymentClient paymentClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final OrderMapper mapper;
    private final OutboxMapper outboxMapper;

    @Override
    @Transactional
    public Integer createOrder(OrderRequest orderRequest) {
        // check if customer exists --> customer-ms
        CustomerResponse customerResponse = customerClient.getCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException(String.format("Cannot create Order:: No customer exist wit this ID %s", orderRequest.customerId())));
        List<ProductPurchaseResponse> purchaseProducts = productClient.getPurchaseProducts(orderRequest.products());// --> with RestTemplate
        Order order = orderRepository.save(mapper.toOrder(orderRequest));
        log.info(" ----> order : {}", order);
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

//        start the payment process --> payment-ms
//        paymentClient.pay(
//                new PaymentRequest(
//                        orderRequest.amount(),
//                        orderRequest.paymentMethod(),
//                        order.getId(),
//                        orderRequest.reference(),
//                        customerResponse
//                )
//        );

        //send order-confirmation into kafka --> notification-ms
//        orderProducer.sendOrderConfirmation(
//                new OrderConfirmation(
//                        order.getId(),
//                        orderRequest.reference(),
//                        orderRequest.paymentMethod(),
//                        orderRequest.amount(),
//                        customerResponse,
//                        purchaseProducts
//                )
//        );

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
