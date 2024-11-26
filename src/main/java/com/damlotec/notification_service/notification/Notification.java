package com.damlotec.notification_service.notification;

import com.damlotec.notification_service.kafka.order.OrderConfirmation;
import com.damlotec.notification_service.kafka.payment.PaymentConfirmation;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor @NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Document
public class Notification {
    private String id;
    private NotificationType notificationType;
    private LocalDateTime notificationDate;
    private OrderConfirmation orderConfirmation;
    private PaymentConfirmation paymentConfirmation;
}
