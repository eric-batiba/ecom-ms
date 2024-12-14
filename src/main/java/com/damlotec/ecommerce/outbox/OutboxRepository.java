package com.damlotec.ecommerce.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox,Integer> {
    List<Outbox> findByStatusFalse();
}
