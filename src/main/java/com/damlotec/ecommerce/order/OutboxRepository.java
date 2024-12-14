package com.damlotec.ecommerce.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox,Integer> {
}
