package com.damlotec.ecommerce.kafka;

import com.avro.OrderConfirmation;
import com.avro.PaymentNotification;
import com.damlotec.ecommerce.email.EmailService;
import com.damlotec.ecommerce.email.TemplateType;
import com.damlotec.ecommerce.notification.Notification;
import com.damlotec.ecommerce.notification.NotificationRepository;
import com.damlotec.ecommerce.notification.NotificationType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    private static final Set<String> INVALID_CUSTOMER_NAMES = Set.of("OM", "MTN", "PAYPAL");


    @RetryableTopic
    @KafkaListener(topics = "payment-topic")
    public void consumePayment(PaymentNotification paymentConfirmation, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) Long offset) throws JsonProcessingException {

        try {
            log.info("Received: {} from {} offset {}", objectMapper.writeValueAsString(paymentConfirmation), topic, offset);
            if (!INVALID_CUSTOMER_NAMES.contains(paymentConfirmation.getCustomerFirstName().toString()))
                throw new IllegalArgumentException("Customer first name not present");
            notificationRepository.save(
                    Notification.builder()
                            .notificationType(NotificationType.PAYMENT_CONFIRMATION)
                            .paymentConfirmation(paymentConfirmation)
                            .notificationDate(LocalDateTime.now())
                            .build()
            );
            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", paymentConfirmation.getCustomerFirstName() + " " + paymentConfirmation.getCustomerLastName());
            variables.put("totalAmount", paymentConfirmation.getTotalAmount());
            variables.put("orderRef", paymentConfirmation.getOrderRef());
            emailService.sendEmail(
                    paymentConfirmation.getCustomerEmail().toString(),
                    variables,
                    TemplateType.PAYMENT_CONFIRMATION
            );
        } catch (Exception e) {
            log.error("Error processing payment confirmation [topic: {}]: {}", topic, e.getMessage(), e);
            throw e;
        }
    }

    @RetryableTopic
    @KafkaListener(topics = "order-topic")
    public void consumeOrder(OrderConfirmation orderConfirmation,
                             @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                             @Header(KafkaHeaders.OFFSET) Long offset) {
        try {
            log.info("Received : {} from {} offset {}", objectMapper.writeValueAsString(orderConfirmation), topic, offset);

            if (INVALID_CUSTOMER_NAMES.contains(orderConfirmation.getCustomer().getFirstName().toString()))
                throw new IllegalArgumentException("Customer first name not present");
            notificationRepository.save(
                    Notification.builder()
                            .notificationType(NotificationType.ORDER_CONFIRMATION)
                            .orderConfirmation(orderConfirmation)
                            .notificationDate(LocalDateTime.now())
                            .build()
            );
            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", orderConfirmation.getCustomer().getFirstName() + " " + orderConfirmation.getCustomer().getLastName());
            variables.put("totalAmount", orderConfirmation.getTotalAmount());
            variables.put("orderRef", orderConfirmation.getReference());
            variables.put("orderDate", LocalDateTime.now());
            variables.put("shippingAddress", orderConfirmation.getCustomer().getAddress());
            variables.put("products", orderConfirmation.getProducts());
            emailService.sendEmail(
                    orderConfirmation.getCustomer().getEmail().toString(),
                    variables,
                    TemplateType.ORDER_CONFIRMATION
            );
        } catch (Exception e) {
            log.error("Error processing order confirmation [topic: {}]: {}", topic, e.getMessage(), e);
        }
    }


    @DltHandler
    public void listenDLT(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) Long offset) {
        log.info("Received DLT from {} offset {}", topic, offset);

    }

}
