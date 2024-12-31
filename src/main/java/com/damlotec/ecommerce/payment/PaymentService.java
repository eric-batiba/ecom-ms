package com.damlotec.ecommerce.payment;

import com.damlotec.ecommerce.exception.PaymentAlreadyExist;
import com.damlotec.ecommerce.exception.PaymentFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    public static final boolean BOOLEAN = true;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper mapper;
    private final OutboxRepository outboxRepository;
    private final OutboxMapper outboxMapper;

    @Transactional
    public Integer createPayment(PaymentRequest request) {
        log.info("Creating payment for order {}", request.toString());
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(request.orderId());
        if (existingPayment.isPresent()) {
            log.info("Paiement déjà effectué pour orderId: {}", request.orderId());
            throw new PaymentAlreadyExist(String.format("Payment already done with orderId: %s", request.orderId()));
        }

        // mock payment call (ex: API call)
        boolean paymentSuccess = simulatePaymentProcessing(request);
        if (!paymentSuccess) throw new PaymentFailException("Processing Payment failed : it's return false");
        Payment mapperPayment = mapper.toPayment(request);
        Payment payment = paymentRepository.save(mapperPayment);
        payment.setStatus(PaymentStatus.SUCCESS);
        Outbox outbox = outboxMapper.toOutbox(request);
        outboxRepository.save(outbox);
        log.info("Payment successfully process with orderId: {}", request.orderId());
        return payment.getId();
    }

    private boolean simulatePaymentProcessing(PaymentRequest request) {
        // mock successful payment call (implement integration by gateway payment)
        log.info("Simulating payment process with orderId: {}", request.orderId());
        return BOOLEAN;
    }
}
