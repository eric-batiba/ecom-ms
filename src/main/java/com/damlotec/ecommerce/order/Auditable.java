package com.damlotec.ecommerce.order;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
    @CreatedDate()
    @Column(name = "order_create", nullable = false, updatable = false)
    LocalDateTime orderCreate;
    @LastModifiedDate
    @Column(name = "order_update")
    LocalDateTime orderUpdate;
}
