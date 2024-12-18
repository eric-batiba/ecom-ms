package com.damlotec.ecommerce.payment;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString @Builder
@Entity
@Table(name = "payment")
public class Payment extends Auditable{
    @Id
    @SequenceGenerator(name = "payment_seq", sequenceName = "payment_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator ="payment_seq")
    private Integer id;
    @Column(nullable = false)
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    @Column(unique = true, nullable = false)
    private Integer orderId;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private Boolean successful = Boolean.FALSE;
}
