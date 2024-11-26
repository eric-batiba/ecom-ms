package com.damlotec.notification_service.kafka;

import com.damlotec.notification_service.email.EmailService;
import com.damlotec.notification_service.email.TemplateType;
import com.damlotec.notification_service.kafka.order.OrderConfirmation;
import com.damlotec.notification_service.kafka.payment.PaymentConfirmation;
import com.damlotec.notification_service.notification.Notification;
import com.damlotec.notification_service.notification.NotificationRepository;
import com.damlotec.notification_service.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
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

    @KafkaListener(topics = "payment-topic")
    public void consumePayment(PaymentConfirmation paymentConfirmation) {
        log.info("Received Payment Confirmation: {}", paymentConfirmation);
        notificationRepository.save(
                Notification.builder()
                        .notificationType(NotificationType.PAYMENT_CONFIRMATION)
                        .paymentConfirmation(paymentConfirmation)
                        .notificationDate(LocalDateTime.now())
                        .build()
        );
        Map<String,Object> variables = new HashMap<>();
        variables.put("customerName",paymentConfirmation.customerFirstName()+" "+paymentConfirmation.customerLastName());
        variables.put("totalAmount",paymentConfirmation.totalAmount());
        variables.put("orderRef",paymentConfirmation.orderRef());
        emailService.sendEmail(
                paymentConfirmation.customerEmail(),
                variables,
                TemplateType.PAYMENT_CONFIRMATION
        );
    }

    @KafkaListener(topics = "order-topic")
    public void consumeOrder(OrderConfirmation orderConfirmation) {
        log.info("Received Order Confirmation: {}", orderConfirmation);
        notificationRepository.save(
                Notification.builder()
                        .notificationType(NotificationType.ORDER_CONFIRMATION)
                        .orderConfirmation(orderConfirmation)
                        .notificationDate(LocalDateTime.now())
                        .build()
        );
        Map<String,Object> variables = new HashMap<>();
        variables.put("customerName",orderConfirmation.customer().firstName()+" "+orderConfirmation.customer().lastName());
        variables.put("totalAmount",orderConfirmation.totalAmount());
        variables.put("orderRef",orderConfirmation.reference());
        variables.put("orderDate",LocalDateTime.now());
        variables.put("shippingAddress",orderConfirmation.customer().address());
        variables.put("products",orderConfirmation.products());
        emailService.sendEmail(
                orderConfirmation.customer().email(),
                variables,
                TemplateType.PAYMENT_CONFIRMATION
        );
    }

}
