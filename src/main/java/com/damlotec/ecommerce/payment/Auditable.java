package com.damlotec.ecommerce.payment;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable {
    @CreatedDate()
    @Column(name = "payment_create", nullable = false, updatable = false)
    LocalDateTime paymentCreate;
    @LastModifiedDate
    @Column(name = "payment_update")
    LocalDateTime paymentUpdate;
}
