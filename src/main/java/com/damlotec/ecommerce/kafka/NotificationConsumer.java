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
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
    public void consumePayment(ConsumerRecord<String, PaymentNotification> paymentConfirmation, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) Long offset) throws JsonProcessingException {
        try {
            log.info("Key: {}, Value: {}", paymentConfirmation.key(), paymentConfirmation.value());
            log.info("Received: {} from {} offset {}", objectMapper.writeValueAsString(paymentConfirmation.value()), topic, offset);
            if (!INVALID_CUSTOMER_NAMES.contains(paymentConfirmation.value().getCustomerFirstName().toString()))
                throw new IllegalArgumentException("Customer first name not present");
            notificationRepository.save(
                    Notification.builder()
                            .notificationType(NotificationType.PAYMENT_CONFIRMATION)
                            .paymentConfirmation(paymentConfirmation.value())
                            .notificationDate(LocalDateTime.now())
                            .build()
            );
            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", paymentConfirmation.value().getCustomerFirstName() + " " + paymentConfirmation.value().getCustomerLastName());
            variables.put("totalAmount", paymentConfirmation.value().getTotalAmount());
            variables.put("orderRef", paymentConfirmation.value().getOrderRef());
            emailService.sendEmail(
                    paymentConfirmation.value().getCustomerEmail().toString(),
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
    public void consumeOrder(ConsumerRecord<String, OrderConfirmation> orderConfirmation,
                             @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                             @Header(KafkaHeaders.OFFSET) Long offset) {
        try {
            log.info("Key: {} value: {}", orderConfirmation.key(), orderConfirmation.value());
            log.info("Received : {} from {} offset {}", objectMapper.writeValueAsString(orderConfirmation), topic, offset);

            if (INVALID_CUSTOMER_NAMES.contains(orderConfirmation.value().getCustomer().getFirstName().toString()))
                throw new IllegalArgumentException("Customer first name not present");
            notificationRepository.save(
                    Notification.builder()
                            .notificationType(NotificationType.ORDER_CONFIRMATION)
                            .orderConfirmation(orderConfirmation.value())
                            .notificationDate(LocalDateTime.now())
                            .build()
            );
            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", orderConfirmation.value().getCustomer().getFirstName() + " " + orderConfirmation.value().getCustomer().getLastName());
            variables.put("totalAmount", orderConfirmation.value().getTotalAmount());
            variables.put("orderRef", orderConfirmation.value().getReference());
            variables.put("orderDate", LocalDateTime.now());
            variables.put("shippingAddress", orderConfirmation.value().getCustomer().getAddress());
            variables.put("products", orderConfirmation.value().getProducts());
            emailService.sendEmail(
                    orderConfirmation.value().getCustomer().getEmail().toString(),
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
