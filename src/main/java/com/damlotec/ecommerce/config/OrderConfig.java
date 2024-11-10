package com.damlotec.ecommerce.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "order.config")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class OrderConfig {
    private String customerUrl;
    private String productUrl;
    private String paymentUrl;
    private String test;
}
