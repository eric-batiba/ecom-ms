package com.damlotec.ecommerce.outbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "outbox")
public class Outbox {
    @Id
    @SequenceGenerator(name = "outbox_seq", sequenceName = "outbox_seq_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outbox_seq")
    private Integer id;
    @Column(nullable = false)
    private String aggregateId;
    @Column(nullable = false)
    private String messageType;
    @Column(nullable = false)
    private String payload;
    @Column(nullable = false)
    private Boolean status;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
