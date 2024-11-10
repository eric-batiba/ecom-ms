package com.damlotec.ecommerce.payment;

import com.damlotec.ecommerce.kafka.PaymentNotification;
import com.damlotec.ecommerce.kafka.PaymentProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;
    @Mock
    PaymentProducer paymentProducer;
    @Mock
    PaymentMapper mapper;
    @Captor
    ArgumentCaptor<PaymentNotification> paymentNotificationArgumentCaptor;
    @InjectMocks
    PaymentService underTest;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Test
    void testConnexionDb() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void shouldCreatePayment() {
        //given
        Customer customer = new Customer("1", "btb", "eric", "btb@gmail.com");
        PaymentRequest paymentRequest = new PaymentRequest(new BigDecimal(100), PaymentMethod.CREDIT_CARD, 1, "order-ref", customer);
        Payment payment = Payment.builder().totalAmount(new BigDecimal(100)).paymentMethod(PaymentMethod.CREDIT_CARD).orderId(1).build();
        Payment paymentSaved = Payment.builder().id(1).totalAmount(new BigDecimal(100)).paymentMethod(PaymentMethod.CREDIT_CARD).orderId(1).build();

        when(mapper.toPayment(paymentRequest)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(paymentSaved);
        //when
        Integer result = underTest.createPayment(paymentRequest);
        //verify
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(paymentProducer, times(1)).sendPaymentNotification(paymentNotificationArgumentCaptor.capture());
        PaymentNotification paymentNotification = paymentNotificationArgumentCaptor.getValue();
        //then
        assertThat(result).isNotNull().isEqualTo(1);
        assertThat(paymentNotification.orderId()).isEqualTo(1);

    }
}