package com.damlotec.ecommerce.notification;

import com.avro.OrderConfirmation;
import com.avro.PaymentNotification;
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
    private PaymentNotification paymentNotification;
}
