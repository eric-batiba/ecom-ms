package com.damlotec.ecommerce.kafka;

import com.avro.PaymentNotification;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = PaymentNotificationDeserializer.class)
public class PaymentNotificationWrapper extends PaymentNotification {
}
