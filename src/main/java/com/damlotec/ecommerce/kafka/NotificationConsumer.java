package com.damlotec.ecommerce.kafka;

import com.avro.OrderConfirmation;
import com.avro.PaymentNotification;
import com.damlotec.ecommerce.email.EmailService;
import com.damlotec.ecommerce.email.TemplateType;
import com.damlotec.ecommerce.notification.Notification;
import com.damlotec.ecommerce.notification.NotificationRepository;
import com.damlotec.ecommerce.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

//    @RetryableTopic
    @KafkaListener(topics = "payment-topic")
    public void consumePayment(ConsumerRecord<String, PaymentNotification> paymentNotification, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){
        try {
            log.info("Value: {}",paymentNotification.value());
            notificationRepository.save(
                    Notification.builder()
                            .notificationType(NotificationType.PAYMENT_CONFIRMATION)
                            .paymentNotification(paymentNotification.value())
                            .notificationDate(LocalDateTime.now())
                            .build()
            );
            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", paymentNotification.value().getCustomerFirstName() + " " + paymentNotification.value().getCustomerLastName());
            variables.put("totalAmount", paymentNotification.value().getTotalAmount());
            variables.put("orderRef", paymentNotification.value().getOrderRef());
            emailService.sendEmail(
                    paymentNotification.value().getCustomerEmail().toString(),
                    variables,
                    TemplateType.PAYMENT_CONFIRMATION
            );
        } catch (Exception e) {
            log.error("Error processing payment confirmation [topic: {}]: {}", topic, e.getMessage(), e);
            throw e;
        }
    }

//    @RetryableTopic
    @KafkaListener(topics = "order-topic")
    public void consumeOrder(ConsumerRecord<String, OrderConfirmation> orderConfirmation,
                             @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("value: {}", orderConfirmation.value());

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

}
