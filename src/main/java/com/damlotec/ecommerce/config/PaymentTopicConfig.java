package com.damlotec.ecommerce.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class PaymentTopicConfig {
    public static final String PAYMENT_TOPIC = "payment-topic";

    @Bean
    public NewTopic topic() {
        return TopicBuilder
                .name(PAYMENT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
