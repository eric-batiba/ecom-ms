package com.damlotec.ecommerce.payment;

import com.damlotec.ecommerce.kafka.PaymentNotification;
import com.damlotec.ecommerce.kafka.PaymentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;
    private final PaymentMapper mapper;

    public Integer createPayment(PaymentRequest request) {
        log.info("Creating payment for order {}", request);
        Payment payment = paymentRepository.save(mapper.toPayment(request));

        // send payment confirmation to kafka
        paymentProducer.sendPaymentNotification(
                new PaymentNotification(
                        request.totalAmount(),
                        request.paymentMethod(),
                        request.orderId(),
                        request.orderRef(),
                        request.customer().firstName(),
                        request.customer().lastName(),
                        request.customer().email()
                )
        );

        return payment.getId();
    }
}
