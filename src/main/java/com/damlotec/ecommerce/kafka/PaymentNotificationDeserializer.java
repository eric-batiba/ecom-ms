package com.damlotec.ecommerce.kafka;

import com.damlotec.ecommerce.payment.PaymentMethod;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;

public class PaymentNotificationDeserializer extends JsonDeserializer<PaymentNotification> {
    @Override
    public PaymentNotification deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        var node = (JsonNode) mapper.readTree(parser);

//        extract and process fields from the JSON node
        BigDecimal totalAmount = node.has("totalAmount") && node.get("totalAmount").isNumber()
                ? node.get("totalAmount").decimalValue()
                : BigDecimal.ZERO;
        PaymentMethod paymentMethod = node.has("paymentMethod") && node.get("paymentMethod").isTextual()
                ? PaymentMethod.valueOf(node.get("paymentMethod").asText())
                : null;

        Integer orderId = node.has("orderId") && node.get("orderId").isInt()
                ? node.get("orderId").asInt()
                : null;

        String orderRef = node.has("reference") && node.get("reference").isTextual()
                ? node.get("reference").asText()
                : null;

        // Extract nested customer object
        JsonNode customerNode = node.get("customer");
        String customerFirstName = customerNode != null && customerNode.has("firstName") && customerNode.get("firstName").isTextual()
                ? customerNode.get("firstName").asText()
                : null;

        String customerLastName = customerNode != null && customerNode.has("lastName") && customerNode.get("lastName").isTextual()
                ? customerNode.get("lastName").asText()
                : null;

        String customerEmail = customerNode != null && customerNode.has("email") && customerNode.get("email").isTextual()
                ? customerNode.get("email").asText()
                : null;

        // Construct and return the PaymentNotification object
        return new PaymentNotification(totalAmount, paymentMethod, orderId, orderRef, customerFirstName, customerLastName, customerEmail);
    }
}
