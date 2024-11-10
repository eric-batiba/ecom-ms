package com.damlotec.ecommerce.order;

import com.damlotec.ecommerce.config.OrderConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RestController
@RequestScope
@RequiredArgsConstructor
public class OrderConfigController {
    private final OrderConfig orderConfig;

    @GetMapping("/config")
    public OrderConfig getOrderConfig() {
        return orderConfig;
    }
}
