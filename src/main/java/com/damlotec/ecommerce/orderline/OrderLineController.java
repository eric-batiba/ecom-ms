package com.damlotec.ecommerce.orderline;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order-lines")
@RequiredArgsConstructor
public class OrderLineController {
    private final OrderLineService orderLineService;

    @PostMapping
    public ResponseEntity<Integer> createOrderLine(OrderLineRequest orderLineRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderLineService.saveOrderLine(orderLineRequest));
    }

    @GetMapping("/order/{order-id}")
    public ResponseEntity<List<OrderLineResponse>> getAllOrderLineByOderId(@PathVariable("order-id") Integer orderId) {
        return ResponseEntity.ok(orderLineService.getAllOrderLineByOderId(orderId));
    }
}
