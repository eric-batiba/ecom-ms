package com.damlotec.ecommerce.notification;

import com.damlotec.ecommerce.kafka.order.OrderConfirmation;
import com.damlotec.ecommerce.kafka.payment.PaymentConfirmation;
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
